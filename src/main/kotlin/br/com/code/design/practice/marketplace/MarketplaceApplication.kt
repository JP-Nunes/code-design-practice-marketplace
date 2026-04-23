package br.com.code.design.practice.marketplace

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MarketplaceApplication

fun main(args: Array<String>) {
	runApplication<MarketplaceApplication>(*args)
}
