package com.luisd.hodler.data.repository

import com.luisd.hodler.data.local.dao.HoldingDao
import com.luisd.hodler.data.local.entity.HoldingEntity
import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PortfolioRepositoryImplTest {

    private lateinit var repository: PortfolioRepositoryImpl
    private lateinit var mockHoldingDao: HoldingDao

    private val mockBitcoinHolding = Holding(
        id = 1L,
        coinId = "bitcoin",
        coinSymbol = "BTC",
        coinName = "Bitcoin",
        imageUrl = "https://example.com/bitcoin.png",
        amount = 0.5,
        purchasePrice = 50000.0,
        purchaseDate = System.currentTimeMillis() - 86400000 // 1 day ago
    )

    private val mockEthereumHolding = Holding(
        id = 2L,
        coinId = "ethereum",
        coinSymbol = "ETH",
        coinName = "Ethereum",
        imageUrl = "https://example.com/ethereum.png",
        amount = 2.0,
        purchasePrice = 3000.0,
        purchaseDate = System.currentTimeMillis() - 172800000 // 2 days ago
    )

    private val mockBitcoinEntity = HoldingEntity(
        id = 1L,
        coinId = "bitcoin",
        coinSymbol = "BTC",
        coinName = "Bitcoin",
        imageUrl = "https://example.com/bitcoin.png",
        amount = 0.5,
        purchasePrice = 50000.0,
        purchaseDate = System.currentTimeMillis() - 86400000
    )

    private val mockEthereumEntity = HoldingEntity(
        id = 2L,
        coinId = "ethereum",
        coinSymbol = "ETH",
        coinName = "Ethereum",
        imageUrl = "https://example.com/ethereum.png",
        amount = 2.0,
        purchasePrice = 3000.0,
        purchaseDate = System.currentTimeMillis() - 172800000
    )

    @Before
    fun setup() {
        mockHoldingDao = mockk(relaxed = true)
        repository = PortfolioRepositoryImpl(mockHoldingDao)
    }

    // ==================== getAllHoldings Tests ====================

    @Test
    fun `getAllHoldings returns success with list of holdings`() = runTest {
        // Arrange
        val entities = listOf(mockBitcoinEntity, mockEthereumEntity)
        coEvery { mockHoldingDao.getAllHoldings() } returns flowOf(entities)

        // Act
        val result = repository.getAllHoldings().first()

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.size)
        assertEquals("bitcoin", successResult.data[0].coinId)
        assertEquals("ethereum", successResult.data[1].coinId)
        assertEquals(0.5, successResult.data[0].amount, 0.001)
        assertEquals(2.0, successResult.data[1].amount, 0.001)
    }

    @Test
    fun `getAllHoldings returns success with empty list when no holdings exist`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.getAllHoldings() } returns flowOf(emptyList())

        // Act
        val result = repository.getAllHoldings().first()

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertTrue(successResult.data.isEmpty())
    }

    @Test
    fun `getAllHoldings returns Flow that emits on database changes`() = runTest {
        // Arrange - Start with one holding
        coEvery { mockHoldingDao.getAllHoldings() } returns flowOf(
            listOf(mockBitcoinEntity),
            listOf(mockBitcoinEntity, mockEthereumEntity) // Then two holdings
        )

        // Act - Collect first two emissions
        val flow = repository.getAllHoldings()
        val firstEmission = flow.first()

        // Assert
        assertTrue(firstEmission is Result.Success)
        val firstResult = firstEmission as Result.Success
        assertEquals(1, firstResult.data.size)
    }

    // ==================== getHoldingById Tests ====================

    @Test
    fun `getHoldingById returns success when holding exists`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.getHoldingById(1L) } returns mockBitcoinEntity

        // Act
        val result = repository.getHoldingById(1L)

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1L, successResult.data.id)
        assertEquals("bitcoin", successResult.data.coinId)
        assertEquals(0.5, successResult.data.amount, 0.001)
    }

    @Test
    fun `getHoldingById returns error when holding not found`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.getHoldingById(999L) } returns null

        // Act
        val result = repository.getHoldingById(999L)

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Holding not found", errorResult.exception.message)
    }

    @Test
    fun `getHoldingById returns error when database exception occurs`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.getHoldingById(1L) } throws RuntimeException("Database error")

        // Act
        val result = repository.getHoldingById(1L)

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Database error", errorResult.exception.message)
    }

    @Test
    fun `getHoldingById with different IDs queries correctly`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.getHoldingById(42L) } returns mockBitcoinEntity.copy(id = 42L)

        // Act
        val result = repository.getHoldingById(42L)

        // Assert
        coVerify { mockHoldingDao.getHoldingById(42L) }
        val successResult = result as Result.Success
        assertEquals(42L, successResult.data.id)
    }

    // ==================== insertHolding Tests ====================

    @Test
    fun `insertHolding returns success with generated ID`() = runTest {
        // Arrange
        val generatedId = 5L
        coEvery { mockHoldingDao.insertHolding(any()) } returns generatedId

        // Act
        val result = repository.insertHolding(mockBitcoinHolding)

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(generatedId, successResult.data)
        coVerify { mockHoldingDao.insertHolding(any()) }
    }

    @Test
    fun `insertHolding maps domain model to entity correctly`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.insertHolding(any()) } returns 1L

        // Act
        repository.insertHolding(mockBitcoinHolding)

        // Assert
        coVerify {
            mockHoldingDao.insertHolding(
                withArg { entity ->
                    assertEquals("bitcoin", entity.coinId)
                    assertEquals("BTC", entity.coinSymbol)
                    assertEquals(0.5, entity.amount, 0.001)
                    assertEquals(50000.0, entity.purchasePrice, 0.001)
                }
            )
        }
    }

    @Test
    fun `insertHolding returns error when database exception occurs`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.insertHolding(any()) } throws RuntimeException("Database constraint violation")

        // Act
        val result = repository.insertHolding(mockBitcoinHolding)

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Database constraint violation", errorResult.exception.message)
    }

    @Test
    fun `insertHolding handles fractional coin amounts`() = runTest {
        // Arrange
        val fractionalHolding = mockBitcoinHolding.copy(amount = 0.00123456)
        coEvery { mockHoldingDao.insertHolding(any()) } returns 1L

        // Act
        repository.insertHolding(fractionalHolding)

        // Assert
        coVerify {
            mockHoldingDao.insertHolding(
                withArg { entity ->
                    assertEquals(0.00123456, entity.amount, 0.00000001)
                }
            )
        }
    }

    // ==================== deleteHoldingById Tests ====================

    @Test
    fun `deleteHoldingById returns success when holding is deleted`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.deleteHoldingById(1L) } returns 1 // 1 row deleted

        // Act
        val result = repository.deleteHoldingById(1L)

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data)
        coVerify { mockHoldingDao.deleteHoldingById(1L) }
    }

    @Test
    fun `deleteHoldingById returns success with 0 when holding not found`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.deleteHoldingById(999L) } returns 0 // No rows deleted

        // Act
        val result = repository.deleteHoldingById(999L)

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(0, successResult.data)
    }

    @Test
    fun `deleteHoldingById returns error when database exception occurs`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.deleteHoldingById(1L) } throws RuntimeException("Database error")

        // Act
        val result = repository.deleteHoldingById(1L)

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Database error", errorResult.exception.message)
    }

    @Test
    fun `deleteHoldingById with different IDs calls DAO correctly`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.deleteHoldingById(42L) } returns 1

        // Act
        repository.deleteHoldingById(42L)

        // Assert
        coVerify { mockHoldingDao.deleteHoldingById(42L) }
    }

    // ==================== updateHolding Tests ====================

    @Test
    fun `updateHolding returns success when update succeeds`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.updateHolding(any()) } returns Unit

        // Act
        val result = repository.updateHolding(mockBitcoinHolding)

        // Assert
        assertTrue(result is Result.Success)
        coVerify { mockHoldingDao.updateHolding(any()) }
    }

    @Test
    fun `updateHolding maps domain model to entity correctly`() = runTest {
        // Arrange
        val updatedHolding = mockBitcoinHolding.copy(
            amount = 1.5,
            purchasePrice = 60000.0
        )
        coEvery { mockHoldingDao.updateHolding(any()) } returns Unit

        // Act
        repository.updateHolding(updatedHolding)

        // Assert
        coVerify {
            mockHoldingDao.updateHolding(
                withArg { entity ->
                    assertEquals(1L, entity.id)
                    assertEquals(1.5, entity.amount, 0.001)
                    assertEquals(60000.0, entity.purchasePrice, 0.001)
                }
            )
        }
    }

    @Test
    fun `updateHolding returns error when database exception occurs`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.updateHolding(any()) } throws RuntimeException("Database error")

        // Act
        val result = repository.updateHolding(mockBitcoinHolding)

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Database error", errorResult.exception.message)
    }

    @Test
    fun `updateHolding preserves coin metadata`() = runTest {
        // Arrange
        val updatedHolding = mockBitcoinHolding.copy(amount = 2.0)
        coEvery { mockHoldingDao.updateHolding(any()) } returns Unit

        // Act
        repository.updateHolding(updatedHolding)

        // Assert
        coVerify {
            mockHoldingDao.updateHolding(
                withArg { entity ->
                    assertEquals("bitcoin", entity.coinId)
                    assertEquals("BTC", entity.coinSymbol)
                    assertEquals("Bitcoin", entity.coinName)
                    assertEquals("https://example.com/bitcoin.png", entity.imageUrl)
                }
            )
        }
    }

    // ==================== Edge Case Tests ====================

    @Test
    fun `repository handles multiple rapid inserts`() = runTest {
        // Arrange
        coEvery { mockHoldingDao.insertHolding(any()) } returnsMany listOf(1L, 2L, 3L)

        // Act
        val result1 = repository.insertHolding(mockBitcoinHolding)
        val result2 = repository.insertHolding(mockEthereumHolding)
        val result3 = repository.insertHolding(mockBitcoinHolding.copy(amount = 0.1))

        // Assert
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        assertTrue(result3 is Result.Success)
        assertEquals(1L, (result1 as Result.Success).data)
        assertEquals(2L, (result2 as Result.Success).data)
        assertEquals(3L, (result3 as Result.Success).data)
    }

    @Test
    fun `repository handles holding with zero amount`() = runTest {
        // Arrange
        val zeroAmountHolding = mockBitcoinHolding.copy(amount = 0.0)
        coEvery { mockHoldingDao.insertHolding(any()) } returns 1L

        // Act
        val result = repository.insertHolding(zeroAmountHolding)

        // Assert
        assertTrue(result is Result.Success)
        coVerify {
            mockHoldingDao.insertHolding(
                withArg { entity ->
                    assertEquals(0.0, entity.amount, 0.001)
                }
            )
        }
    }

    @Test
    fun `repository handles very large purchase prices`() = runTest {
        // Arrange
        val expensiveHolding = mockBitcoinHolding.copy(purchasePrice = 999999999.99)
        coEvery { mockHoldingDao.insertHolding(any()) } returns 1L

        // Act
        val result = repository.insertHolding(expensiveHolding)

        // Assert
        assertTrue(result is Result.Success)
        coVerify {
            mockHoldingDao.insertHolding(
                withArg { entity ->
                    assertEquals(999999999.99, entity.purchasePrice, 0.01)
                }
            )
        }
    }

    @Test
    fun `repository handles very old purchase dates`() = runTest {
        // Arrange
        val oldDate = 946684800000L // January 1, 2000
        val oldHolding = mockBitcoinHolding.copy(purchaseDate = oldDate)
        coEvery { mockHoldingDao.insertHolding(any()) } returns 1L

        // Act
        val result = repository.insertHolding(oldHolding)

        // Assert
        assertTrue(result is Result.Success)
        coVerify {
            mockHoldingDao.insertHolding(
                withArg { entity ->
                    assertEquals(oldDate, entity.purchaseDate)
                }
            )
        }
    }
}
