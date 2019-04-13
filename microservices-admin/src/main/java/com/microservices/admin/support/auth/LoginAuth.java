package com.microservices.admin.support.auth;

import com.microservices.Microservices;
import com.microservices.admin.base.plugin.shiro.auth.MuitiAuthenticatied;
import com.microservices.admin.service.api.ResService;
import com.microservices.admin.service.api.RoleService;
import com.microservices.admin.service.api.UserService;
import com.microservices.admin.service.entity.model.Res;
import com.microservices.admin.service.entity.model.Role;
import com.microservices.admin.service.entity.model.User;
import com.microservices.admin.service.entity.status.system.UserStatus;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端认证授权
 */
public class LoginAuth implements MuitiAuthenticatied {

	@Override
	public boolean hasToken(AuthenticationToken authenticationToken) {
		String loginName = authenticationToken.getPrincipal().toString();
		UserService sysUserApi = Microservices.service(UserService.class);
		return sysUserApi.hasUser(loginName);
	}

	@Override
	public boolean wasLocked(AuthenticationToken authenticationToken) {
		String loginName = authenticationToken.getPrincipal().toString();

		UserService sysUserApi = Microservices.service(UserService.class);
		User sysUser = sysUserApi.findByName(loginName);
		return !sysUser.getStatus().equals(UserStatus.USED);
	}

	@Override
	public AuthenticationInfo buildAuthenticationInfo(AuthenticationToken authenticationToken) {
		String loginName = authenticationToken.getPrincipal().toString();

		UserService sysUserApi = Microservices.service(UserService.class);
		User sysUser = sysUserApi.findByName(loginName);
		String salt2 = sysUser.getSalt2();
		String pwd = sysUser.getPwd();

		return new SimpleAuthenticationInfo(loginName, pwd, ByteSource.Util.bytes(salt2), "ShiroDbRealm");
	}

	@Override
	public AuthorizationInfo buildAuthorizationInfo(PrincipalCollection principals) {
		String loginName = (String) principals.fromRealm("ShiroDbRealm").iterator().next();

		RoleService sysRoleApi = Microservices.service(RoleService.class);
		List<Role> sysRoleList = sysRoleApi.findByUserName(loginName);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		List<String> roleNameList = new ArrayList<String>();
		for (Role sysRole : sysRoleList) {
			roleNameList.add(sysRole.getName());
		}

		ResService sysResService = Microservices.service(ResService.class);
		List<Res> sysResList = sysResService.findByUserNameAndStatusUsed(loginName);
		List<String> urls = new ArrayList<String>();
		for (Res sysRes : sysResList) {
			urls.add(sysRes.getUrl());
		}

		info.addRoles(roleNameList);
		info.addStringPermissions(urls);
		return info;
	}
}
