package concentric.circles.sliced_onion.inventory.internal

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.math.log

@RestController
@RequestMapping("/inventory")
class InventoryController(private val inventoryService: InventoryService) {

    @GetMapping
    fun getInventories(): ResponseEntity<List<InventoryDto>> = ResponseEntity.ok()
        .body(inventoryService.getInventories().map { inventory: Inventory -> InventoryDto(inventory) })

    @PostMapping
    fun createInventory(@RequestBody inventoryDto: InventoryDto): ResponseEntity<InventoryDto?> {
        val inventory = inventoryService.createInventory(inventoryDto) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(InventoryDto(inventory))
    }

    @GetMapping("/{inventoryId}")
    fun getInventory(@PathVariable inventoryId: UUID): ResponseEntity<InventoryDto?> {
        val inventory = inventoryService.getInventory(inventoryId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok().body(InventoryDto(inventory))
    }

    @PutMapping("/{inventoryId}/restock")
    fun increaseInventoryQuantity(
        @PathVariable inventoryId: UUID,
        @RequestBody quantity: Int
    ): ResponseEntity<InventoryDto?> {
        val inventory = inventoryService.increaseInventoryQuantity(inventoryId, quantity)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok().body(InventoryDto(inventory))
    }

    @PostMapping("/verify")
    fun verifyInventory(
        @RequestBody data: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {
            val productIdsAsStrings = (data["productIds"] as? List<*>)
                ?.filterIsInstance<String>()
                ?: throw IllegalArgumentException("Invalid or missing productIds")

            val productIds = productIdsAsStrings.mapNotNull { idString -> UUID.fromString(idString) }

            println(productIds)

            val decreaseStocks = (data["decreaseStocks"] as? Boolean)
                ?: throw IllegalArgumentException("Invalid or missing decreaseStocks")

            inventoryService.verify(productIds, decreaseStocks)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Error verifying inventory: ${e.message}")
        }
    }


}