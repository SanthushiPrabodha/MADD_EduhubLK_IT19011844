//IT19011844-Hemachandra M.G.S.P.- Assignment Component
package com.example.madd_eduhublk_it19011844

import android.content.Intent

class IntentResult{
    companion object {

        fun forAll(): Intent{
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            return intent
        }

        fun forPDF(): Intent{
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            return intent
        }

        fun forImage(): Intent {
            val intent = Intent()
            intent.type = "application/image"
            intent.action = Intent.ACTION_GET_CONTENT
            return intent
        }
    }
}