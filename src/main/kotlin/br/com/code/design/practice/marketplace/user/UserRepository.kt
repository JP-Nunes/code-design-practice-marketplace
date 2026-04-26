package br.com.code.design.practice.marketplace.user

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long>
