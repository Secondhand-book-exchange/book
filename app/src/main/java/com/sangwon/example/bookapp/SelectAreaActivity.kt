package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.opencsv.CSVReader
import com.sangwon.example.bookapp.Adapter.AreaAdapter
import com.sangwon.example.bookapp.databinding.ActivitySelectAreaBinding
import java.io.InputStreamReader


class SelectAreaActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectAreaBinding
    lateinit var area: HashMap<String, HashMap<String, ArrayList<String>>>
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


        val stateAdapter = AreaAdapter()
        val stateList = binding.state
        val cityList = binding.city
        val townList = binding.town
        stateAdapter.setArea(states as ArrayList<String>)
        stateList.adapter = stateAdapter
        stateList.setOnItemClickListener { _, view, position, _ ->
            (stateList.adapter as AreaAdapter).selectedView(position)
            (stateList.adapter as AreaAdapter).notifyDataSetChanged()

            cities = ArrayList()
            state = states[position]
            city = stateAdapter.getItem(position).toString()
            for (city in area[stateAdapter.getItem(position)]?.keys!!) {
                cities.add(city)
            }
            cities.sort()
            cities.remove("")
            cities.add(0, "$state 전체")

            val cityAdapter = AreaAdapter()
            cityAdapter.setArea(cities)
            cityList.adapter = cityAdapter

            if (townList.adapter != null) {
                townList.adapter = null
            }
        }
        cityList.setOnItemClickListener { _, view, position, _ ->
            (cityList.adapter as AreaAdapter).selectedView(position)
            (cityList.adapter as AreaAdapter).notifyDataSetChanged()

            city = cities[position]
            val towns: ArrayList<String> = area[state]?.get(cities[position]) ?: ArrayList()

            val townAdapter = AreaAdapter()
            townAdapter.setArea(towns)
            townList.adapter = townAdapter
        }
        townList.setOnItemClickListener { _, view, position, _ ->
            (townList.adapter as AreaAdapter).selectedView(position)
            (townList.adapter as AreaAdapter).notifyDataSetChanged()

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
        val allArea = csvReader.readAll() as List<Array<String>>

        area = HashMap()
        for (state in states) {
            val city = HashMap<String, ArrayList<String>>()
            area[state] = city
        }
        for (content in allArea) {
            if (area.containsKey(content[1])) {
                var town = ArrayList<String>()
                if (area[content[1]]?.contains(content[2])!!) {
                    town = area[content[1]]?.get(content[2])!!
                    if (town.contains(content[3]))
                        continue
                }
                town.add(content[3])
                area[content[1]]?.put(content[2], town)
            }
        }
        val it: MutableCollection<HashMap<String, ArrayList<String>>> = area.values
        for (city in it) {
            val keys = city.keys
            for (town in keys) {
                city[town]?.sort()
                city[town]?.remove("")
                city[town]?.add(0, "$town 전체")

            }
        }

    }
}