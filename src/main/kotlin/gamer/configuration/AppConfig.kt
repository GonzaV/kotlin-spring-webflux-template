package gamer.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan("gamer")
open class AppConfig {
    @Bean
    open fun mapper(): ObjectMapper {
        return GenericMapper.mapper
    }

    object GenericMapper {
        val mapper: ObjectMapper

        fun buildMapper(camelCase: Boolean = false): ObjectMapper {
            val objectMapper = jacksonObjectMapper()
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            if (!camelCase) objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            objectMapper.registerModule(JavaTimeModule())
            return objectMapper
        }

        init {
            mapper = buildMapper()
        }
    }
}