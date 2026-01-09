package com.timeblocks.utils

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.timeblocks.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер для работы с Google Play Billing.
 */
@Singleton
class BillingManager @Inject constructor(
    private val context: Context,
    private val userRepository: UserRepository
) {

    private var billingClient: BillingClient
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    init {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        
        startConnection()
    }

    /**
     * Слушатель обновления покупок
     */
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Timber.d("Purchase successful")
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.d("User canceled purchase")
            }
            else -> {
                Timber.e("Purchase failed: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Начать подключение к Billing Client
     */
    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Billing connection established")
                    queryAvailableProducts()
                    checkPremiumStatus()
                } else {
                    Timber.e("Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Timber.w("Billing service disconnected")
                // Попытка переподключения
                startConnection()
            }
        })
    }

    /**
     * Запросить доступные продукты
     */
    private fun queryAvailableProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(Constants.Billing.PRODUCT_MONTHLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(Constants.Billing.PRODUCT_YEARLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _products.value = productDetailsList
                Timber.d("Products loaded: ${productDetailsList.size}")
            } else {
                Timber.e("Failed to load products: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Проверить статус Premium
     */
    private fun checkPremiumStatus() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val isPremium = purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                _isPremium.value = isPremium
                
                // Обновить в репозитории
                val userId = userRepository.getCurrentUserId()
                if (userId != null) {
                    kotlinx.coroutines.MainScope().launch {
                        userRepository.updatePremiumStatus(userId, isPremium)
                    }
                }
                
                Timber.d("Premium status: $isPremium")
            }
        }
    }

    /**
     * Обработать покупку
     */
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _isPremium.value = true
            
            // Обновить статус в репозитории
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                kotlinx.coroutines.MainScope().launch {
                    userRepository.updatePremiumStatus(userId, true)
                }
            }

            // Подтвердить покупку
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }

            Timber.d("Purchase handled: ${purchase.products}")
        }
    }

    /**
     * Подтвердить покупку
     */
    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Timber.d("Purchase acknowledged")
            } else {
                Timber.e("Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Начать покупку подписки
     */
    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: "")
                        .build()
                )
            )
            .build()

        val result = billingClient.launchBillingFlow(activity, params)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            Timber.e("Failed to launch billing flow: ${result.debugMessage}")
        }
    }

    /**
     * Отменить подписку
     */
    fun cancelSubscription() {
        // В реальном приложении нужно открыть Play Store для управления подписками
        // Это не может быть сделано программно
        Timber.d("Cancel subscription requested")
    }

    /**
     * Проверить, активна ли подписка
     */
    suspend fun checkSubscriptionActive(): Boolean {
        val userId = userRepository.getCurrentUserId() ?: return false
        return userRepository.isPremium(userId)
    }

    /**
     * Закрыть соединение
     */
    fun close() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }
}