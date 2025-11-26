package com.example.razomua.ui.nfc
import android.content.Intent
import android.nfc.NdefMessage
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NfcReadActivity : AppCompatActivity() {

    private lateinit var nfcHelper: NfcHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcHelper = NfcHelper(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val id = nfcHelper.readProfileId(intent)
        if (id != null) {
            Toast.makeText(this, "ID: $id", Toast.LENGTH_LONG).show()
        }
    }
}
