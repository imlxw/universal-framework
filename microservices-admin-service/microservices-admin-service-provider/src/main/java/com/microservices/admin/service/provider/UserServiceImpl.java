package com.microservices.admin.service.provider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.microservices.admin.service.api.UserRoleService;
import com.microservices.admin.service.api.UserService;
import com.microservices.admin.service.entity.model.User;
import com.microservices.admin.service.entity.model.UserRole;
import com.microservices.admin.service.entity.status.system.UserOnlineStatus;
import com.microservices.aop.annotation.Bean;
import com.microservices.core.rpc.annotation.MicroservicesrpcService;
import com.microservices.db.model.Columns;
import com.microservices.service.MicroservicesServiceBase;

/**
 * 用户信息
 */
@Bean
@Singleton
@MicroservicesrpcService
public class UserServiceImpl extends MicroservicesServiceBase<User> implements UserService {

	@Inject
	private UserRoleService userRoleService;

	@Override
	public Page<User> findPage(User user, int pageNumber, int pageSize) {
		Columns columns = Columns.create();

		if (StrKit.notBlank(user.getName())) {
			columns.like("name", "%" + user.getName() + "%");
		}
		if (StrKit.notBlank(user.getPhone())) {
			columns.like("phone", "%" + user.getPhone() + "%");
		}
		return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
	}

	@Override
	public boolean hasUser(String name) {
		return findByName(name) != null;
	}

	@Override
	public User findByName(String name) {
		return DAO.findFirstByColumn("name", name);
	}

	@Override
	public boolean saveUser(User user, Long[] roles) {
		String pwd = user.getPwd();

		if (StrKit.notBlank(pwd)) {
			String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
			SimpleHash hash = new SimpleHash("md5", pwd, salt2, 2);
			pwd = hash.toHex();
			user.setPwd(pwd);
			user.setSalt2(salt2);
		}

		user.setOnlineStatus(UserOnlineStatus.OFFLINE);
		user.setCreatedate(new Date());
		user.setLastUpdTime(new Date());
		user.setNote("保存系统用户");

		return Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				if (!user.save()) {
					return false;
				}

				if (roles != null) {
					List<UserRole> list = new ArrayList<UserRole>();
					for (Long roleId : roles) {
						UserRole userRole = new UserRole();
						userRole.setUserId(user.getId());
						userRole.setRoleId(roleId);
						list.add(userRole);
					}
					int[] rets = userRoleService.batchSave(list);

					for (int ret : rets) {
						if (ret < 1) {
							return false;
						}
					}
				}
				return true;
			}
		});
	}

	@Override
	public boolean updateUser(User user, Long[] roles) {
		String pwd = user.getPwd();
		if (StrKit.notBlank(pwd)) {
			String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
			SimpleHash hash = new SimpleHash("md5", pwd, salt2, 2);
			pwd = hash.toHex();
			user.setPwd(pwd);
			user.setSalt2(salt2);
		} else {
			user.remove("pwd");
		}

		user.setLastUpdTime(new Date());
		user.setNote("修改系统用户");

		return Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				if (!user.update()) {
					return false;
				}

				userRoleService.deleteByUserId(user.getId());

				if (roles != null) {
					List<UserRole> list = new ArrayList<UserRole>();
					for (Long roleId : roles) {
						UserRole userRole = new UserRole();
						userRole.setUserId(user.getId());
						userRole.setRoleId(roleId);
						list.add(userRole);
					}

					int[] rets = userRoleService.batchSave(list);
					for (int ret : rets) {
						if (ret < 1) {
							return false;
						}
					}
				}
				return true;
			}
		});
	}
}