package com.example.razomua.ui.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.razomua.R

class NfcWriteActivity : AppCompatActivity() {

    private lateinit var nfcHelper: NfcHelper
    private lateinit var message: NdefMessage
    private var writingEnabled = false

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(
                this,
                "NFC недоступний на цьому пристрої. Операція неможлива.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setContentView(R.layout.activity_nfc_write)

        nfcHelper = NfcHelper(this)

        val profileId = intent.getIntExtra("PROFILE_ID", 0).toString()
        message = nfcHelper.createNdefMessage(profileId)

        val startButton = findViewById<Button>(R.id.startWriteButton)
        startButton.setOnClickListener {
            writingEnabled = true
            Toast.makeText(this, "Піднесіть телефон до NFC-мітки", Toast.LENGTH_SHORT).show()
        }

        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(
                this,
                "Увімкніть NFC у налаштуваннях, щоб продовжити.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()

        if (nfcAdapter == null) return

        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE
            else
                0
        )

        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (!writingEnabled) return

        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        if (tag == null) {
            Toast.makeText(this, "Помилка: тег не знайдено.", Toast.LENGTH_SHORT).show()
            return
        }

        val success = nfcHelper.writeToTag(message, tag)

        if (success) {
            Toast.makeText(this, "Профіль успішно записано!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Помилка запису.", Toast.LENGTH_SHORT).show()
        }
    }
}
