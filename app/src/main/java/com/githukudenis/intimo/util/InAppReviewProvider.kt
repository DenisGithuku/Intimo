package com.githukudenis.intimo.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class InAppReviewProvider(val context: Context) {

    private val manager: ReviewManager by lazy {
        ReviewManagerFactory.create(context)
    }


    suspend fun requestReviewInfo(): ReviewInfo? {
        return suspendCancellableCoroutine { continuation ->
            val request = manager.requestReviewFlow()

            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // we have a ReviewInfo object
                    val reviewInfo = task.result
                    continuation.resume(reviewInfo)
                } else {
                    // an error occurred
                    Log.d("review info error", task.exception?.localizedMessage.toString())
                    continuation.resume(null)
                }
            }
        }
    }

        fun launchReviewFlow(activity: Activity, reviewInfo: ReviewInfo) {
            val flow = manager.launchReviewFlow(activity, reviewInfo)
            flow.addOnCompleteListener { task ->  }
        }
    }

