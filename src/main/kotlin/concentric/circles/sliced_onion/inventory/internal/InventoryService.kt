package concentric.circles.sliced_onion.inventory.internal

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class InventoryService(private val inventoryRepository: InventoryRepository) {

    fun getInventories(): List<Inventory> = inventoryRepository.findAll()

    fun getInventory(inventoryId: UUID) = inventoryRepository.findByInventoryId(inventoryId)

    fun getInventoryByProductId(productId: UUID) = inventoryRepository.findByProductId(productId)

    @Transactional
    fun createInventory(inventoryDto: InventoryDto): Inventory? {
        val inventory = Inventory(inventoryDto)
        return inventoryRepository.save(inventory)
    }

    fun increaseInventoryQuantity(inventoryId: UUID, quantity: Int): Inventory? {
        val inventory = inventoryRepository.findByInventoryId(inventoryId) ?: return null
        inventory.quantity += quantity
        return inventoryRepository.save(inventory)
    }

    @Transactional
    fun verify(productIds: List<UUID>, decreaseStock: Boolean): Boolean {
        println(productIds)

        for (productId in productIds) {
            println("$productId: ")
            val inventory = inventoryRepository.findByProductId(productId) ?: throw Exception()
            print("found")

            if (decreaseStock){
                if (inventory.quantity <= 0) throw Exception()

                inventory.quantity -= 1
                inventoryRepository.save(inventory)
            }
        }
        return true
    }
}