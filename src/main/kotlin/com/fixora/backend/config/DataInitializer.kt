package com.fixora.backend.config

import com.fixora.backend.domain.entity.StaffRole
import com.fixora.backend.domain.entity.StaffUserEntity
import com.fixora.backend.domain.repository.StaffUserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val staffUserRepository: StaffUserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val admin = staffUserRepository.findByEmail("karim@techcare.com").orElse(null)
        if (admin == null) {
            staffUserRepository.save(
                StaffUserEntity(
                    name = "Admin Owner",
                    email = "karim@techcare.com",
                    passwordHash = passwordEncoder.encode("password123"),
                    role = StaffRole.ROLE_OWNER,
                    branchName = "Main Branch"
                )
            )
        } else {
            admin.passwordHash = passwordEncoder.encode("password123")
            staffUserRepository.save(admin)
        }
    }
}
