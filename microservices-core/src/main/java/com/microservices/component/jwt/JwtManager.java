package com.microservices.component.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.jfinal.json.FastJson;
import com.jfinal.kit.Base64Kit;
import com.microservices.Microservices;
import com.microservices.utils.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

/**
 * @Package com.microservices.web.jwt
 */
public class JwtManager {

	private static final JwtManager me = new JwtManager();

	public static JwtManager me() {
		return me;
	}

	private JwtConfig jwtConfig = Microservices.config(JwtConfig.class);
	private ThreadLocal<Map> jwtThreadLocal = new ThreadLocal<>();

	public void holdJwts(Map map) {
		jwtThreadLocal.set(map);
	}

	public void releaseJwts() {
		jwtThreadLocal.remove();
	}

	public <T> T getPara(String key) {
		Map map = jwtThreadLocal.get();
		return map == null ? null : (T) map.get(key);
	}

	public Map getParas() {
		return jwtThreadLocal.get();
	}

	public boolean isEnable() {
		return jwtConfig.isEnable();
	}

	public String getHttpHeaderName() {
		return jwtConfig.getHttpHeaderName();
	}

	public Map parseJwtToken(String token) {
		SecretKey secretKey = generalKey();
		try {
			Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

			String subject = claims.getSubject();

			if (StringUtils.isBlank(subject)) {
				return null;
			}

			return FastJson.getJson().parse(subject, HashMap.class);

		} catch (SignatureException | MalformedJwtException e) {
			// don't trust the JWT!
			// jwt 签名错误或解析错误，可能是伪造的，不能相信
		} catch (ExpiredJwtException e) {
			// jwt 已经过期
		} catch (Throwable ex) {
			// 其他错误
		}

		return null;
	}

	public String createJwtToken(Map map) {

		String subject = FastJson.getJson().toJson(map);
		SecretKey secretKey = generalKey();

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		JwtBuilder builder = Jwts.builder().setIssuedAt(now).setSubject(subject).signWith(signatureAlgorithm, secretKey);

		if (jwtConfig.getValidityPeriod() > 0) {
			long expMillis = nowMillis + jwtConfig.getValidityPeriod();
			builder.setExpiration(new Date(expMillis));
		}

		return builder.compact();
	}

	private SecretKey generalKey() {
		byte[] encodedKey = Base64Kit.decode(jwtConfig.getSecret());
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		return key;
	}

}
