package com.sangwon.example.bookapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.opencsv.CSVReader
import com.sangwon.example.bookapp.databinding.ActivitySelectAreaBinding
import java.io.InputStreamReader


class SelectAreaActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectAreaBinding
    lateinit var area:HashMap<String, HashMap<String, ArrayList<String>>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        areaDatePreprocessing()

//        val stateList = binding.state
//        val cityList = binding.city
//        val townList = binding.town
//        stateList.selectedItem.
    }

    fun areaDatePreprocessing() {
        val states = "경기도, 대구광역시, 충청북도, 충청남도, 제주특별자치도, 강원도, 세종특별자치시, 울산광역시, 대전광역시, 광주광역시, 전라남도, 부산광역시, 전라북도, 경상남도, 서울특별시, 경상북도, 인천광역시".split(", ")

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
                if (area[content[1]]?.contains(content[2])!!) {
                    val town = area[content[1]]?.get(content[2])
                    town?.add(content[3])
                    Log.d("citys",town.toString())
                    area[content[1]]?.put(content[2], town!!)
                }else{
                    val town = ArrayList<String>()
                    town.add(content[3])
                    area[content[1]]?.put(content[2], town)
                }
            }
        }
        Log.d("states",area.keys.toString())
        for (state in area.keys) {
            Log.d("Allcitys",area[state].toString())
        }
//
//        Log.d("towns",area.keys.toString())

    }
}