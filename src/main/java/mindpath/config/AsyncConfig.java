package mindpath.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements  AsyncConfigurer{



    @Bean(name = "uploadTaskExecutor")
    @Primary
    public Executor uploadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("UploadExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        ThreadPoolExecutor threadPoolExecutor = executor.getThreadPoolExecutor();

        if (threadPoolExecutor != null) {
            threadPoolExecutor.getQueue().clear();
            System.out.println("Cleared task queue during executor initialization.");
        }

        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return uploadTaskExecutor();
    }
}