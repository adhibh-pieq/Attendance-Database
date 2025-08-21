package config
import io.dropwizard.core.Configuration
import io.dropwizard.db.DataSourceFactory
import com.fasterxml.jackson.annotation.JsonProperty
class Configuration : Configuration() {
    @JsonProperty("database")
    lateinit var database: DataSourceFactory
}