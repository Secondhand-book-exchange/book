package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.opencsv.CSVReader
import com.sangwon.example.bookapp.databinding.ActivitySelectAreaBinding
import java.io.InputStreamReader


class SelectAreaActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectAreaBinding
    lateinit var area: HashMap<String, HashMap<String, ArrayList<String>>>
    lateinit var cityAdapter: ArrayAdapter<String>
    val states =
        "경기도, 대구광역시, 충청북도, 충청남도, 제주특별자치도, 강원도, 세종특별자치시, 울산광역시, 대전광역시, 광주광역시, 전라남도, 부산광역시, 전라북도, 경상남도, 서울특별시, 경상북도, 인천광역시".split(
            ", "
        )
    private var cities = ArrayList<String>()
    private lateinit var state: String
    private lateinit var city: String
    private lateinit var town: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        areaDatePreprocessing()


        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, states)
        val stateList = binding.state
        val cityList = binding.city
        val townList = binding.town
        stateList.adapter = stateAdapter
        stateList.setOnItemClickListener { _, _, position, _ ->
            cities = ArrayList()
            state = states[position]
            city = stateAdapter.getItem(position).toString()
            for (city in area[stateAdapter.getItem(position)]?.keys!!) {
                cities.add(city)
            }
            cityAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities)
            cityList.adapter = cityAdapter
        }
        cityList.setOnItemClickListener { _, _, position, _ ->
            val towns = area[city]?.get(cities[position]) ?: ArrayList()
            city = cities[position]
            val townAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, towns)
            townList.adapter = townAdapter
        }
        townList.setOnItemClickListener { _, _, position, _ ->
            town = townList.adapter.getItem(position).toString()
        }

        binding.check.setOnClickListener {
            val intent = Intent()
            intent.putExtra(
                "location",
                "$state $city $town"
            )
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun areaDatePreprocessing() {

        val areaInput = baseContext.resources.assets.open("area_library.csv")
        val csvReader = CSVReader(InputStreamReader(areaInput, "EUC-KR"))
        val allContent = csvReader.readAll() as List<Array<String>>

        area = HashMap()
        for (state in states) {
            val city = HashMap<String, ArrayList<String>>()
            area[state] = city
        }
        for (content in allContent) {
            /*Log.d(
                "csv",
                content[0] + " 도: " + content[1] + " 시: " + content[2] + " 동: " + content[3]
            )*/
            if (area.containsKey(content[1])) {
                var town = ArrayList<String>()
                if (area[content[1]]?.contains(content[2])!!) {
                    town = area[content[1]]?.get(content[2])!!
                }
                town?.add(content[3])
                area[content[1]]?.put(content[2], town!!)
            }
        }
//
//        Log.d("towns",area.keys.toString())

    }
}