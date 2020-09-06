/*-
 * #%L
 * Spring Auto REST Docs Kotlin Web MVC Example Project
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package capital.scalable.restdocs.example.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

private const val RESOURCE_ID = "items"

@Configuration
internal class OAuth2ServerConfiguration {

    @Configuration
    @EnableResourceServer
    class ResourceServerConfiguration : ResourceServerConfigurerAdapter() {

        override fun configure(resources: ResourceServerSecurityConfigurer) {
            resources.resourceId(RESOURCE_ID)
        }

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            http
                .authorizeRequests()
                .antMatchers(POST, "/items").authenticated()
                .antMatchers(PUT, "/items/*").authenticated()
                .antMatchers(DELETE, "/items/*").authenticated()
        }
    }

    @Configuration
    @EnableAuthorizationServer
    class AuthorizationServerConfiguration : AuthorizationServerConfigurerAdapter() {

        private val tokenStore = InMemoryTokenStore()

        @Autowired
        @Qualifier("authenticationManagerBean")
        private val authenticationManager: AuthenticationManager? = null

        @Autowired
        private val userDetailsService: CustomUserDetailsService? = null

        @Throws(Exception::class)
        override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
            endpoints
                .tokenStore(this.tokenStore)
                .authenticationManager(this.authenticationManager)
                .userDetailsService(userDetailsService)
        }

        @Throws(Exception::class)
        override fun configure(clients: ClientDetailsServiceConfigurer) {
            clients
                .inMemory()
                .withClient("app")
                .authorizedGrantTypes("password", "refresh_token")
                .authorities("USER")
                .scopes("read", "write")
                .resourceIds(RESOURCE_ID)
                .secret("$2a$10\$UjMybe50F28W0P20YkjV3unT6wXvpwOCvkf0H3uJ2PUk4z66OHQEe")
        }

        @Throws(Exception::class)
        override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
            oauthServer.passwordEncoder(BCryptPasswordEncoder())
        }

        @Bean
        @Primary
        fun tokenServices(): DefaultTokenServices {
            val tokenServices = DefaultTokenServices()
            tokenServices.setSupportRefreshToken(true)
            tokenServices.setTokenStore(this.tokenStore)
            return tokenServices
        }

    }
}
