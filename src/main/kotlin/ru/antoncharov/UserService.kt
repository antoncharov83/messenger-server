package ru.antoncharov

import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation

class UserService(private val keycloak: Keycloak) {
    fun findByUsername(username: String): List<UserRepresentation> =
        keycloak
            .realm("jag-messenger-realm")
            .users()
            .search(username)
}