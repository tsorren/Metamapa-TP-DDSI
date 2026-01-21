package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración para procesamiento asíncrono.
 * Permite que las importaciones de CSV se ejecuten en segundo plano
 * sin mantener la conexión HTTP abierta durante todo el proceso.
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

  /**
   * Executor específico para tareas de importación de datos.
   * Configurado con un pool pequeño ya que las importaciones son intensivas en I/O.
   */
  @Bean(name = "importacionExecutor")
  public Executor importacionExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(10);
    executor.setThreadNamePrefix("importacion-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(60);
    executor.initialize();
    log.info("✓ Executor de importación configurado: core={}, max={}, queue={}",
            executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
    return executor;
  }
}

