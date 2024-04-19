import kotlin.math.max
import kotlin.math.min


fun lz77Compress(input: String, window: Int, buffer: Int): List<Triple<Int, Int, Char>> {
    val compressed = mutableListOf<Triple<Int, Int, Char>>()
    var indexChar = 0

    while (indexChar < input.length) {
        var bestLength = 0
        var bestOffset = 0
        var nextChar = ""

        val maxOffset = min(indexChar, window)
        val maxLength: Int = min(input.length - indexChar, buffer)

        for (offset in 1..maxOffset) {
            for (length in 1..maxLength) {
                val stringWindow: String = input.substring(indexChar - offset, indexChar - offset + length)
                val stringBuffer: String = input.substring(indexChar, indexChar + length)

                if (stringWindow == stringBuffer && length > bestLength) {
                    bestLength = length
                    bestOffset = (offset - window) * (-1)
                    if (indexChar + length < input.length) {
                        nextChar = input[indexChar + length].toString()
                    }
                }
            }
        }
        if (indexChar == input.length - 1) {
            compressed.add(Triple(0, 0, input[indexChar]))
        } else {
            compressed.add(Triple(bestOffset, bestLength, if (bestLength > 0) nextChar else input[indexChar]) as Triple<Int, Int, Char>)
        }
        indexChar += bestLength + 1
    }

    return compressed
}
fun lzssCompress(input: String, max_dictionary:Int, max_buffer:Int): MutableList<Array<String>> {
    val compressed: MutableList<Array<String>> = mutableListOf()
    var dictionary = ""
    var buffer: String = input.substring(0, min(max_buffer, input.length))
    var inputUser = input.substring(buffer.length)

    println()
    while (buffer.isNotEmpty()) {
        var offset = 0
        var length = 0
        for (i in 1..buffer.length) {
            val subStr = buffer.substring(0, i)
            val position = dictionary.lastIndexOf(subStr)
            if (position != -1) {
                offset = max_dictionary - dictionary.length + position
                length = subStr.length
            } else {
                break
            }
        }
        val code: String = if (length > 0) {
            "1<$offset,$length>"
        } else {
            "0'" + buffer[0] + "'"
        }
        compressed.add(arrayOf(code))
        print("( $code ) ")

        val shiftSize = max(length.toDouble(), 1.0).toInt()
        dictionary += buffer.substring(0, shiftSize)
        if (dictionary.length > max_dictionary) {
            dictionary = dictionary.substring(dictionary.length - max_dictionary)
        }

        buffer = buffer.substring(shiftSize)
        if (buffer.length < max_buffer && inputUser.isNotEmpty()) {
            val addSize: Int = min(max_buffer - buffer.length, inputUser.length)
            buffer += inputUser.substring(0, addSize)
            inputUser = inputUser.substring(addSize)
        }
    }
    println()
    return compressed
}

fun lz78Compress(input: String): List<Pair<Int, Char>> {
    val compressed = mutableListOf<Pair<Int, Char>>()
    val dictionary = mutableMapOf<String, Int>()
    var currentIndex = 0

    while (currentIndex < input.length) {
        var length = 0
        var offset = 0

        for (i in currentIndex..<input.length) {
            val substring = input.substring(currentIndex, i + 1)

            if (!dictionary.containsKey(substring)) {
                dictionary[substring] = dictionary.size + 1
                length = substring.length
                offset = dictionary[substring.substring(0, length - 1)] ?: 0
                break
            }
        }

        if (length > 0) {
            compressed.add(Pair(offset, input[currentIndex + length - 1]))
            currentIndex += length
        } else {
            compressed.add(Pair(offset, input[currentIndex]))
            currentIndex++
        }
    }

    return compressed
}


fun main() {
    val input = readlnOrNull() ?: ""
    print("Размер словаря: ")
    val dictionary = readlnOrNull() ?: "0"
    print("Размер буфера: ")
    val buffer = readlnOrNull() ?: "0"

    val lz77Result = lz77Compress(input, dictionary.toInt(), buffer.toInt())
    println("LZ77 compressed: $lz77Result")

    val lzssResult = lzssCompress(input, dictionary.toInt(), buffer.toInt())
    println("LZSS compressed: $lzssResult")

    val lz78Result = lz78Compress(input)
    println("LZ78 compressed: $lz78Result")
}
