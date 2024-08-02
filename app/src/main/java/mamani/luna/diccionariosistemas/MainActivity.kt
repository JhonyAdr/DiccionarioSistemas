package mamani.luna.diccionariosistemas

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mamani.luna.diccionariosistemas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: MeaningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener{
            val word = binding.searchInput.text.toString()
            getMeaning(word)
        }
        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


         }


        }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getMeaning(word: String) {
            setInprogress(true)
            GlobalScope.launch {
                try{
                    val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                    if(response.body()==null)
                        throw(Exception())
                    runOnUiThread {
                        setInprogress(false)
                        response.body()?.first()?.let{
                            setIU(it)
                        }

                    }

                }catch (e : Exception){
                    runOnUiThread {
                        setInprogress(false)
                        Toast.makeText(applicationContext, "Algo Salió mal", Toast.LENGTH_SHORT).show()
                    }
                    }

            }


    }

    private fun setIU(response : WordResult){
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text = response.phonetic
        adapter.updateNewData(response.meanings)


    }

    private fun setInprogress(inProgress : Boolean){
        if (inProgress){
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }

    }


}



