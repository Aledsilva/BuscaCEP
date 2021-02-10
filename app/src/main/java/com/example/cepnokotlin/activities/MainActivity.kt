package com.example.cepnokotlin.activities

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cepnokotlin.R
import com.example.cepnokotlin.api.ApiService
import com.example.cepnokotlin.model.CEPResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

const val BASE_URL = "https://viacep.com.br/ws/"
private lateinit var cep: String
private lateinit var resultado: String
private lateinit var logradouro: String
private lateinit var bairro: String
private lateinit var cidade: String
private lateinit var uf: String
private var cepErrado by Delegates.notNull<Boolean>()
private lateinit var mediaPlayer: MediaPlayer
private var totalTime : Int = 0

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        executarSom()

        bt_buscar.setOnClickListener {
            cep = edit_cep.text.toString()
            getCurrentData()
        }
        txt_resultado.setOnClickListener {

            if (cepErrado == true){
                Toast.makeText(applicationContext, "Este não é um endereço válido!", Toast.LENGTH_LONG).show()
            }else{
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("IR PARA ENDEREÇO SELECIONADO")
                alertDialog.setMessage("Você será redirecionado para GoogleMaps, deseja continuar?")
                alertDialog.setPositiveButton("Sim") { dialog, wich ->
                    resultado = txt_resultado.text.toString()
                    vamoLa(resultado)
                    Toast.makeText(applicationContext, "Redirecionadooo!", Toast.LENGTH_SHORT).show()
                }
                alertDialog.setNegativeButton("Não") { dialog, wich ->
                }
                val dialog: AlertDialog = alertDialog.create()
                dialog.show()
            }
        }
    }

    fun Context.vamoLa(destino: String) = startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=$destino")
        ).setPackage("com.google.android.apps.maps")
    )

    private fun getCurrentData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val response: Response<CEPResponse> = retrofit.recuperarCEP(cep).awaitResponse()
            if (response.isSuccessful) {
                val response: CEPResponse = response.body()!!

                withContext(Dispatchers.Main) {
                    logradouro = response.logradouro
                    bairro = response.bairro
                    cidade = response.localidade
                    uf = response.uf
                    cepErrado = response.erro
                    when (cepErrado == true) {
                        true ->
                            txt_resultado.text = "Por favor, digite um CEP válido"
                        else -> {
                            txt_aviso.visibility = View.VISIBLE
                            txt_resultado.text = "$logradouro,$bairro - $cidade-$uf"
                        }
                    }
                }
            }
        }
    }

    fun executarSom(){
        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.smack)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f, 0.5f)
        totalTime = mediaPlayer.duration
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer.release()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (mediaPlayer != null) {
            mediaPlayer.start()
        }
    }
}