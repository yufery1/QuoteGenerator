package com.example.quotegenerator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.quotegenerator.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Используем свойство делегата для ленивой инициализации ViewBinding
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val quotes: List<Int> = listOf(
        R.string.quote_string,
        R.string.quote_string2,
        R.string.quote_string3,
        R.string.quote_string4,
    )

    private var quoteIndex = 0
    private var currentQuote = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        quoteOnAppLoaded()
        setupNewQuoteButton()
    }

    private fun typeText(text: String) {
        // Очищаем текущий текст перед началом набора нового
        currentQuote = ""

        val textDelay: Long = 50L

        GlobalScope.launch(Dispatchers.IO) {
            for (i in text.indices) {
                currentQuote += text[i]
                Thread.sleep(textDelay)
            }
        }

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                binding.fabText.text = "$currentQuote|"
                handler.postDelayed(this, 10)
                if (text == currentQuote) {
                    handler.removeCallbacks(this)
                    binding.fabText.text = currentQuote
                    binding.fabQuote.isEnabled = true
                }
            }
        }
        handler.postDelayed(runnable, 0)
    }

    private fun setupNewQuoteButton() {
        binding.fabQuote.setOnClickListener {
            binding.fabQuote.isEnabled = false
            if (quoteIndex == quotes.size) {
                quoteOnAppLoaded()
            } else {
                typeText(getString(quotes[quoteIndex]))
                quoteIndex++
            }
        }
    }

    private fun quoteOnAppLoaded() {
        binding.fabQuote.isEnabled = false
        quoteIndex = 0
        quotes.shuffled()
        typeText(getString(quotes[quoteIndex]))
        quoteIndex++
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_share -> {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, currentQuote)
                }
                startActivity(Intent.createChooser(shareIntent, "Share this quote!"))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
