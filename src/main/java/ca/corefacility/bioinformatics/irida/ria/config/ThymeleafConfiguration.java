package ca.corefacility.bioinformatics.irida.ria.config;

/**
 * Replace JSP's with Thymeleaf templating.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ThymeleafConfiguration {
//	private static final Logger logger = LoggerFactory.getLogger(ThymeleafConfiguration.class);
//
//	private static final String TEMPLATE_MODE = "HTML5";
//	private static final String TEMPLATE_PREFIX = "/static/pages/";
//	private static final String TEMPLATE_SUFFIX = ".html";
//	private static final Long TEMPLATE_CACHE_TIME = 3600000L;
//	private static final int TEMPLATE_ORDER = 1;
//	private static final boolean TEMPLATE_NOT_CACHEABLE = false;
//
//	@Autowired
//	private Environment env;
//
//	@Bean
//	public MessageSource messageSource() {
//		logger.info("Configuring ReloadableResourceBundleMessageSource.");
//
//		// TODO: Create one for each 'page'
//		String[] resources= {"classpath:/i18n/login", "classpath:/i18n/dashboard"};
//
//		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
//		source.setBasenames(resources);
//		source.setDefaultEncoding("UTF-8");
//		return source;
//	}
//
//	/**
//	 * Create a new {@link ServletContextTemplateResolver} and set defaults.
//	 *
//	 * @return {@link ServletContextTemplateResolver}
//	 */
//	@Bean
//	@Description("Thymeleaf template resolver serving HTML 5")
//	public ServletContextTemplateResolver templateResolver() {
//		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
//		resolver.setPrefix(TEMPLATE_PREFIX);
//		resolver.setSuffix(TEMPLATE_SUFFIX);
//		resolver.setTemplateMode(TEMPLATE_MODE);
//		resolver.setOrder(TEMPLATE_ORDER);
//
//		// Determine the spring profile that is being run.
//		// If it is in development we do not want the templates cached
//		if (env.acceptsProfiles(WebConfigurer.SPRING_PROFILE_PRODUCTION)) {
//			resolver.setCacheTTLMs(TEMPLATE_CACHE_TIME);
//		} else {
//			resolver.setCacheable(TEMPLATE_NOT_CACHEABLE);
//		}
//		return resolver;
//	}
//
//	@Bean
//	public SpringTemplateEngine templateEngine() {
//		SpringTemplateEngine engine = new SpringTemplateEngine();
//		engine.setTemplateResolver(templateResolver());
//		engine.setAdditionalDialects(additionalDialects());
//		return engine;
//	}
//
//	@Bean
//	public LiteDeviceDelegatingViewResolver liteDeviceDelegatingViewResolver() {
//		ThymeleafViewResolver delegate = new ThymeleafViewResolver();
//		delegate.setTemplateEngine(templateEngine());
//		delegate.setOrder(1);
//		LiteDeviceDelegatingViewResolver resolver = new LiteDeviceDelegatingViewResolver(delegate);
//		resolver.setMobilePrefix("mobile/");
//		resolver.setTabletPrefix("tablet/");
//		return resolver;
//	}
//
//	private Set<IDialect> additionalDialects() {
//		Set<IDialect> dialects = new HashSet<>();
//		dialects.add(new SpringSecurityDialect());
//		return dialects;
//	}
}
