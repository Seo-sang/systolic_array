package ids

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers


class SystolicTensorArrayTest extends AnyFreeSpec with Matchers {

  "check systolic tensor array behavior" in {
    val blockSize = 2
    val PESize = 2
    val vectorSize = 2
    val width = 32


    val inputMatrix: Vector[Vector[UInt]] = Vector(
      Vector(2.U(width.W), 2.U(width.W), 2.U(width.W), 2.U(width.W), 1.U(width.W), 2.U(width.W), 1.U(width.W), 4.U(width.W)),
      Vector(2.U(width.W), 3.U(width.W), 1.U(width.W), 1.U(width.W), 4.U(width.W), 1.U(width.W), 1.U(width.W), 4.U(width.W)),
      Vector(2.U(width.W), 2.U(width.W), 4.U(width.W), 2.U(width.W), 3.U(width.W), 3.U(width.W), 2.U(width.W), 4.U(width.W)),
      Vector(1.U(width.W), 2.U(width.W), 2.U(width.W), 4.U(width.W), 2.U(width.W), 4.U(width.W), 4.U(width.W), 4.U(width.W)),
      Vector(4.U(width.W), 2.U(width.W), 1.U(width.W), 2.U(width.W), 3.U(width.W), 3.U(width.W), 2.U(width.W), 4.U(width.W)),
      Vector(1.U(width.W), 4.U(width.W), 1.U(width.W), 4.U(width.W), 1.U(width.W), 2.U(width.W), 3.U(width.W), 1.U(width.W)),
      Vector(4.U(width.W), 4.U(width.W), 1.U(width.W), 1.U(width.W), 2.U(width.W), 2.U(width.W), 4.U(width.W), 3.U(width.W)),
      Vector(3.U(width.W), 1.U(width.W), 1.U(width.W), 3.U(width.W), 3.U(width.W), 2.U(width.W), 4.U(width.W), 3.U(width.W)),
    )

    val weightMatrix: Vector[Vector[UInt]] = Vector(
      Vector(2.U(width.W), 3.U(width.W), 4.U(width.W), 4.U(width.W), 2.U(width.W), 1.U(width.W), 2.U(width.W), 4.U(width.W)),
      Vector(1.U(width.W), 1.U(width.W), 1.U(width.W), 1.U(width.W), 2.U(width.W), 3.U(width.W), 1.U(width.W), 4.U(width.W)),
      Vector(1.U(width.W), 2.U(width.W), 1.U(width.W), 4.U(width.W), 1.U(width.W), 4.U(width.W), 2.U(width.W), 3.U(width.W)),
      Vector(2.U(width.W), 4.U(width.W), 3.U(width.W), 2.U(width.W), 4.U(width.W), 2.U(width.W), 3.U(width.W), 4.U(width.W)),
      Vector(4.U(width.W), 2.U(width.W), 3.U(width.W), 1.U(width.W), 2.U(width.W), 4.U(width.W), 3.U(width.W), 2.U(width.W)),
      Vector(4.U(width.W), 4.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W), 2.U(width.W), 3.U(width.W), 2.U(width.W)),
      Vector(1.U(width.W), 1.U(width.W), 4.U(width.W), 4.U(width.W), 1.U(width.W), 4.U(width.W), 2.U(width.W), 4.U(width.W)),
      Vector(2.U(width.W), 4.U(width.W), 3.U(width.W), 2.U(width.W), 2.U(width.W), 3.U(width.W), 2.U(width.W), 1.U(width.W)),
    )

    val outputMatrix: Vector[Vector[UInt]] = Vector(
      Vector(33.U(width.W), 47.U(width.W), 45.U(width.W), 41.U(width.W), 35.U(width.W), 44.U(width.W), 35.U(width.W), 44.U(width.W)),
      Vector(39.U(width.W), 44.U(width.W), 47.U(width.W), 36.U(width.W), 35.U(width.W), 51.U(width.W), 37.U(width.W), 45.U(width.W)),
      Vector(48.U(width.W), 60.U(width.W), 61.U(width.W), 58.U(width.W), 45.U(width.W), 66.U(width.W), 50.U(width.W), 60.U(width.W)),
      Vector(50.U(width.W), 65.U(width.W), 70.U(width.W), 60.U(width.W), 52.U(width.W), 67.U(width.W), 54.U(width.W), 66.U(width.W)),
      Vector(49.U(width.W), 60.U(width.W), 66.U(width.W), 54.U(width.W), 46.U(width.W), 56.U(width.W), 48.U(width.W), 59.U(width.W)),
      Vector(32.U(width.W), 42.U(width.W), 47.U(width.W), 41.U(width.W), 40.U(width.W), 48.U(width.W), 37.U(width.W), 58.U(width.W)),
      Vector(41.U(width.W), 50.U(width.W), 63.U(width.W), 56.U(width.W), 41.U(width.W), 59.U(width.W), 43.U(width.W), 66.U(width.W)),
      Vector(44.U(width.W), 54.U(width.W), 65.U(width.W), 54.U(width.W), 43.U(width.W), 57.U(width.W), 47.U(width.W), 60.U(width.W)),
    )



    simulate(new SystolicTensorArray(blockSize, PESize, vectorSize, width)) { instance =>

      //result 2사분면
      matrixmultiply(inputRow=0, inputCol=0, weightRow=0, weightCol=0, outputRow=0, outputCol=0)
      println("result1 완료")
      //result 1사분면
      matrixmultiply(inputRow=0, inputCol=0, weightRow=0, weightCol=4, outputRow=0, outputCol=4)
      println("result2 완료")
      //result 3사분면
      matrixmultiply(inputRow=4, inputCol=0, weightRow=0, weightCol=0, outputRow=4, outputCol=0)
      println("result3 완료")
      //result 4사분면
      matrixmultiply(inputRow=4, inputCol=0, weightRow=0, weightCol=4, outputRow=4, outputCol=4)
      println("result4 완료")

      //4x4 결과를 내는 4x8 * 8x4 matrix multiply
      def matrixmultiply(inputRow:Int, inputCol: Int, weightRow: Int, weightCol: Int, outputRow:Int, outputCol:Int): Unit = {

        /*****첫 번째 matrix 연산*****/

        //clock #0
        println("clock 0")
        initialize(0, vectorSize)
        dataFeeding(0, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #1
        println("clock 1")
        initialize(1, vectorSize)
        dataFeeding(1, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()


        //clock #2
        println("clock 2")
        initialize(2, vectorSize)
        dataFeeding(2, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        /*****2번째 matrix 연산****/

        //clock #3
        println("clock 3")
        initialize(3, vectorSize)
        dataFeeding(0, inputRow, inputCol + (vectorSize * PESize), weightRow + (vectorSize * PESize), weightCol)
        instance.clock.step()



        //clock #4
        println("clock 4")
        dataFeeding(1, inputRow, inputCol + (vectorSize * PESize), weightRow + (vectorSize * PESize), weightCol)
        instance.clock.step()


        //clock #5
        println("clock 5")
        dataFeeding(2, inputRow, inputCol + (vectorSize * PESize), weightRow + (vectorSize * PESize), weightCol)
        instance.clock.step()

        /*----------OUTPUT 시작-----------*/
        //clock #6
        println("clock 6")
        //output 확인
        instance.io.outputVector(0).expect(outputMatrix(outputRow)(outputCol))
        instance.io.outputVector(1).expect(outputMatrix(outputRow)(outputCol + 1))
        instance.io.outputVector(2).expect(outputMatrix(outputRow + 1)(outputCol))
        instance.io.outputVector(3).expect(outputMatrix(outputRow + 1)(outputCol + 1))

        println(
          outputMatrix(outputRow)(outputCol), " ",
          outputMatrix(outputRow)(outputCol + 1), " ",
          outputMatrix(outputRow + 1)(outputCol), " ",
          outputMatrix(outputRow + 1)(outputCol + 1), " "
        )
        println("---------------------------------------------")
        instance.clock.step()


        //clock #7
        println("clock 7")
        //output 확인
        instance.io.outputVector(4).expect(outputMatrix(outputRow)(outputCol + 2))
        instance.io.outputVector(5).expect(outputMatrix(outputRow)(outputCol + 3))
        instance.io.outputVector(6).expect(outputMatrix(outputRow + 1)(outputCol + 2))
        instance.io.outputVector(7).expect(outputMatrix(outputRow + 1)(outputCol + 3))
        //previous input 전달
        instance.io.controlVector(1).poke(false.B)

        println(
          outputMatrix(outputRow)(outputCol + 2), " ",
          outputMatrix(outputRow)(outputCol + 3), " ",
          outputMatrix(outputRow + 1)(outputCol + 2), " ",
          outputMatrix(outputRow + 1)(outputCol + 3), " "
        )
        println("---------------------------------------------")
        instance.clock.step()


        //clock #8
        println("clock 8")
        //output 확인
        instance.io.outputVector(4).expect(outputMatrix(outputRow + 2)(outputCol))
        instance.io.outputVector(5).expect(outputMatrix(outputRow + 2)(outputCol + 1))
        instance.io.outputVector(6).expect(outputMatrix(outputRow + 3)(outputCol))
        instance.io.outputVector(7).expect(outputMatrix(outputRow + 3)(outputCol + 1))
        instance.io.outputVector(8).expect(outputMatrix(outputRow + 2)(outputCol + 2))
        instance.io.outputVector(9).expect(outputMatrix(outputRow + 2)(outputCol + 3))
        instance.io.outputVector(10).expect(outputMatrix(outputRow + 3)(outputCol + 2))
        instance.io.outputVector(11).expect(outputMatrix(outputRow + 3)(outputCol + 3))
        println(
          outputMatrix(outputRow+ 2)(outputCol), " ",
          outputMatrix(outputRow + 2)(outputCol + 1), " ",
          outputMatrix(outputRow + 3)(outputCol), " ",
          outputMatrix(outputRow + 3)(outputCol + 1), " ",
          outputMatrix(outputRow + 2)(outputCol + 2), " ",
          outputMatrix(outputRow + 2)(outputCol + 3), " ",
          outputMatrix(outputRow + 3)(outputCol + 2), " ",
          outputMatrix(outputRow + 3)(outputCol + 3), " "
        )
        println("---------------------------------------------")

        instance.clock.step()

      }

      def initialize(pos: Int, blockSize: Int): Unit = {
        for(i <- 0 until blockSize) {
          val enableIdx = pos * blockSize - (i * (blockSize - 1))
          if(enableIdx >= 0 && enableIdx < blockSize * blockSize) {
            instance.io.resetVector(enableIdx).poke(true.B)
            instance.io.controlVector(enableIdx).poke(true.B)
          }
          val disableIdx = enableIdx - blockSize
          if(disableIdx >= 0 && disableIdx < blockSize * blockSize) {
            instance.io.resetVector(disableIdx).poke(false.B)
          }
        }
      }


      def dataFeeding(startIdx: Int, inputRow: Int, inputCol: Int, weightRow: Int, weightCol: Int): Unit = {
        for(i <- 0 until blockSize) {
          var startRow = inputRow + i * PESize
          var startCol = inputCol + vectorSize * (startIdx - i)
          var row = 0
          var col = 0

          for(p <- 0 until PESize) {
            for(v <- 0 until vectorSize) {
              row = startRow + p
              col = startCol + v

              if(row >= inputRow && row < inputRow + PESize * blockSize && col >= inputCol && col < inputCol + vectorSize * blockSize) {
                instance.io.inputVector(i * PESize * vectorSize + p * vectorSize + v).poke(inputMatrix(row)(col))
              }
              else {
                instance.io.inputVector(i * PESize * vectorSize + p * vectorSize + v).poke(0.U(width.W))
              }
            }
          }

          startRow = weightRow + vectorSize * (startIdx - i)
          startCol = weightCol + i * PESize

          for(p <- 0 until PESize) {
            for(v <- 0 until vectorSize) {
              row = startRow + v
              col = startCol + p

              if(row >= weightRow && row  < weightRow + blockSize * vectorSize && col >= weightCol && col < weightCol + PESize * blockSize) {
                instance.io.weightVector(i * PESize * vectorSize + p * vectorSize + v).poke(weightMatrix(row)(col))
              }
              else {
                instance.io.weightVector(i * PESize * vectorSize + p * vectorSize + v).poke(0.U(width.W))
              }
            }
          }

        }
      }
    }
  }
}
