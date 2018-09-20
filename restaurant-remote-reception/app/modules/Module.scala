package modules

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import utils.Foo

class Module extends AbstractModule {
    override def configure() = {
        bind(classOf[Foo])
            .annotatedWith(Names.named("HealthCheck"))
            .toInstance(new Foo("Health Check Done"))
    }
}
