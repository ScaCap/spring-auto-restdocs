/*-
 * #%L
 * Spring Auto REST Docs Kotlin Web MVC Example Project
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
internal class CustomUserDetailsService : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        // Usually, you would consult a database, but for simplicity we hardcode a user
        // with username "test" and password "test".
        return if ("test" == username) {
            User.withDefaultPasswordEncoder()
                    .username(username)
                    .password("test")
                    .authorities("USER")
                    .build()
        } else {
            throw UsernameNotFoundException(String.format("User %s does not exist!", username))
        }
    }
}
