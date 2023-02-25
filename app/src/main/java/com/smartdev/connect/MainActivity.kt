package com.smartdev.connect

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartdev.connect.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    var output:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        binding.btnCopy.setOnClickListener {

            if(output.isEmpty())
                return@setOnClickListener
            try{
                output?.copyAllNumbers()

            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }

        }
        binding.btnBack.setOnClickListener {

            var pairsData = binding.inputPairs.text.toString()
            var numbersData = binding.inputNumbers.text.toString()
            if(TextUtils.isEmpty(pairsData))
            {
                Toast.makeText(applicationContext, "Enter pairs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(numbersData))
            {
                Toast.makeText(applicationContext, "Enter numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
try {
            pairsData= pairsData.trim().filterRawData()
            numbersData= numbersData.trim().filterRawData()

                val splitBy = pairsData.splitBy()
                val numsplitBy = numbersData.splitBy()
                val joinToString =
                    backTransformNumbers(pairsData.split(splitBy).map { it.isContainDSpace() },
                        numbersData.split(numsplitBy)
                            .map { it.isContainDSpace() }).map { it.toString() }
                        .joinToString(",")
                output = joinToString
                binding.txtOutput.setText(joinToString)
            }catch (e:Exception)
            {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Something wrong with input numbers..", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnFront.setOnClickListener {

          var pairsData = binding.inputPairs.text.toString().trim()
          var numbersData = binding.inputNumbers.text.toString().trim()
            if(TextUtils.isEmpty(pairsData))
          {
              Toast.makeText(applicationContext, "Enter pairs", Toast.LENGTH_SHORT).show()
              return@setOnClickListener
          }
            if(TextUtils.isEmpty(numbersData))
            {
                Toast.makeText(applicationContext, "Enter numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {

                GlobalScope.launch(Dispatchers.Unconfined) {

                    pairsData = pairsData.trim().filterRawData()
                    numbersData = numbersData.trim().filterRawData()
                    val splitBy = pairsData.splitBy()
                    val numsplitBy = numbersData.splitBy()
                    val joinToString =
                        transformNumbers(pairsData.split(splitBy).map { it.isContainDSpace() },
                            numbersData.split(numsplitBy)
                                .map { it.isContainDSpace() }).map { it.toString() }
                            .joinToString(",")
                    output = joinToString
               withContext(Dispatchers.Main)
               {
                   binding.txtOutput.setText(joinToString)
               }
                }
            }catch (e:Exception)
            {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Something wrong with input numbers..", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun String.filterRawData():String
    {
        var finalresult=this

        if(this.contains("\n"))
        {
            if(this.contains(",")||this.contains("-")) {
                finalresult = this.replace("\n", "")
            }
            else{
                finalresult = this.replace("\n", ",")
            }

        }
        if(finalresult.endsWith(","))
        {
            finalresult=finalresult.dropLast(1)
        }
        return finalresult
    }

    fun String.removeEXtracharacters():String
    {
      if(this.contains(",\n")){
            return this.replace("\n","")
        }
        return  this
    }

    fun String.isContainDSpace():Int
    {
        if(this.contains(" "))
        {
            return this.replace(" ", "").toInt()
        }
        else  if(this.contains("\n")){
           return this.replace("\n","").toInt()
        }
        return  this.toInt()
    }
    private fun setClipboard(context: Context, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context.getSystemService(CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(applicationContext, "Copied data successfully", Toast.LENGTH_SHORT).show()
        }
    }
    fun String.copyAllNumbers()
    {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("data", this)
        clipboard.setPrimaryClip(clip)
    }

    fun String.splitBy():String{
        var separation=
            if(this.contains(","))
            ","
        else if(this.contains("-")) "-"
        else if(this.contains("\n")) "\n"
        else if(this.contains(",\n")) ",\n"
        else if(this.contains(", \n")) ", \n"
        else " "
        return separation;
    }




    fun transformNumbers(pair: List<Int>, numbers: List<Int>): List<Int> {
        val result = mutableListOf<Int>()
        pair.forEach { aNum ->
            numbers.forEach { bNum ->
                if (bNum.toString().substring(0, 2) == aNum.toString().reversed()) {

                    val firstTwoDigits = aNum.toString().substring(0, 2)
                    val transformedNum = firstTwoDigits + bNum.toString().substring(2)
                    result.add(transformedNum.toInt())
//                 result.add(bNum.toString().reversed().toInt())
                }
            }
        }

        return result
    }
    fun backTransformNumbers(pair: List<Int>, numbers: List<Int>): List<Int> {
        val result = mutableListOf<Int>()
        pair.forEach { aNum ->
            numbers.forEach { bNum ->
                if (bNum.toString().substring(2) == aNum.toString().reversed()) {

                    val firstTwoDigits = aNum.toString().substring( 0,2)
                    val transformedNum =  bNum.toString().substring(2)+firstTwoDigits
                    result.add(transformedNum.toInt())
//                 result.add(bNum.toString().reversed().toInt())
                }
            }
        }

        return result
    }
}