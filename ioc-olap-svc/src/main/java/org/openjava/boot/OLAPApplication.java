package org.openjava.boot;

import com.openjava.framework.user.CloudUserProvider;
import com.openjava.framework.user.LmAuthorityPersistent;
import com.openjava.framework.validate.RedisSessionVaidator;
import org.ljdp.cache.spring.CacheKeyGenerator;
import org.ljdp.common.spring.SpringContext;
import org.ljdp.core.db.jpa.JPASessionFactoryRouter;
import org.ljdp.secure.validate.SessionValidator;
import org.mybatis.spring.annotation.MapperScan;
import org.openjava.boot.util.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan(basePackages= {
		"org.ljdp.plugin.batch.persistent",
		"org.ljdp.support.attach.domain",
		"com.openjava.**.domain"
})
@EnableJpaRepositories(
		basePackages={"org.ljdp.support.**.repository",
				"com.openjava.**.repository"},
		repositoryFactoryBeanClass=org.ljdp.core.spring.data.LjdpJpaRepositoryFactoryBean.class)
@MapperScan({
		"com.openjava.**.dao",
		"com.openjava.**.mapper"
})
@EnableTransactionManagement(mode=AdviceMode.ASPECTJ)
@SpringBootApplication(
		scanBasePackages={
				"org.ljdp.support.**.component",
				"org.ljdp.support.**.service",
//				"org.ljdp.support.**.controller",
				"com.openjava.**.service",
				"com.openjava.**.component",
				"com.openjava.**.common",
				"com.openjava.**.dao",
				"com.openjava.**.mapper",
				"com.openjava.**.api",
				"org.openjava.boot.conf",
				})
@ServletComponentScan(basePackages= {
				"org.ljdp.support.web.listener2"})
@ImportResource("classpath:springconfig/transaction.xml")
@EnableCaching
public class OLAPApplication {

	//============缓存配置=================
	@Bean
	public KeyGenerator cacheKeyGenerator(){//缓存key生成者
		CacheKeyGenerator cacheKeyGenerator = new CacheKeyGenerator();
		return cacheKeyGenerator;
	}

	@Bean("cacheManager")
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		return CacheBuilder.defaultRedisCacheManager(factory);
	}


	//==============会话认证=====================
	@Value("${ljdp.security.api.skey}")
	private String apiSkey;
	@Bean
	public SessionValidator sessionValidator() {
		return new IocRedisSessionVaidator(apiSkey);
//		return new EhcacheSessionValidator();
	}
	
	@Bean
	public LmAuthorityPersistent authorityPersistent() {
		return new LmAuthorityPersistent();
	}
	
	//============LJDP相关配置=======================
	@Bean("db.SessionFactoryRouter")
	public JPASessionFactoryRouter sessionFactoryRouter() {
		return new JPASessionFactoryRouter();
	}
	
	@Bean("web.UserProvider")
	public CloudUserProvider webUserProvider() {
		return new CloudUserProvider();
	}
	
	@Bean
	public LjdpBootRunner ljdpRunner() {
		return new LjdpBootRunner();
	}
	
	@Bean
	public SpringContext springContext() {
		return SpringContext.getEmbedInstance();
	}

    public static void main(String[] args) {
        SpringApplication.run(OLAPApplication.class, args);
    }

}
