package cloud.operon.platform;

import cloud.operon.platform.config.ApplicationProperties;
import cloud.operon.platform.config.DefaultProfileUtil;
import cloud.operon.platform.domain.Operino;
import cloud.operon.platform.domain.OperinoComponent;
import cloud.operon.platform.domain.enumeration.HostingType;
import cloud.operon.platform.domain.enumeration.OperinoComponentType;
import cloud.operon.platform.repository.OperinoRepository;
import cloud.operon.platform.repository.UserRepository;
import io.github.jhipster.config.JHipsterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class OperonCloudPlatformApp {

    private static final Logger log = LoggerFactory.getLogger(OperonCloudPlatformApp.class);

    private final Environment env;

    public OperonCloudPlatformApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes operoncloudplatform.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="http://jhipster.github.io/profiles/">http://jhipster.github.io/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not" +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved into an address
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(OperonCloudPlatformApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        ConfigurableApplicationContext ctx= app.run(args);
        Environment env = ctx.getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}\n\t" +
                "External: \t{}://{}:{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            protocol,
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            env.getActiveProfiles());

        if(Arrays.asList(env.getActiveProfiles()).contains("dev") || Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            // add sample data if none exists
            verifyAndImportOperinos(ctx);
        }
    }

    private static void verifyAndImportOperinos(ConfigurableApplicationContext ctx) {

        OperinoRepository operinoRepository = ctx.getBean(OperinoRepository.class);
        UserRepository userRepository = ctx.getBean(UserRepository.class);
        List<Operino> operinos = operinoRepository.findAll().stream().collect(Collectors.toList());
        log.info("operinos.size() = " + operinos.size());

        if(operinos.size() == 0){
            log.info(String.format("*********** Creating sample operinos as [%s] were found", operinos.size()));

            for(int i=3; i<5; i++){
                Operino operino = new Operino();
                operino.setName("Operino " + i);
                operino.setActive(true);
                operino.setUser(userRepository.findOne(Long.valueOf(String.valueOf(i))));

                for(int j=1; j<4; j++){
                    OperinoComponent component = new OperinoComponent();
                    component.setAvailability(true);
                    if (i==4) {
                        component.setHosting(HostingType.NON_N3);
                    }else {
                        component.setHosting(HostingType.N3);
                    }
                    component.setType(OperinoComponentType.values()[j - 1]);
                    component.setDiskSpace(Long.valueOf(String.valueOf(j * 1000)));
                    component.setRecordsNumber(Long.valueOf(String.valueOf(j * 1000)));
                    component.setTransactionsLimit(Long.valueOf(String.valueOf(j * 1000)));
                    operino.addComponents(component);

                }

                // save operino
                operinoRepository.save(operino);
            }
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
