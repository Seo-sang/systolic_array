package ids

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.matchers.must.Matchers
import org.scalatest.freespec.AnyFreeSpec


class SystolicArrayTest extends AnyFreeSpec with Matchers {

  "check systolic array behavior" in {
    val vectorSize = 5
    val width = 32


    val inputMatrix: Vector[Vector[UInt]] = Vector(
      Vector(1.U(width.W), 2.U(width.W), 1.U(width.W), 1.U(width.W), 4.U(width.W), 4.U(width.W), 4.U(width.W), 3.U(width.W), 1.U(width.W), 1.U(width.W)),
      Vector(3.U(width.W), 1.U(width.W), 1.U(width.W), 1.U(width.W), 4.U(width.W), 1.U(width.W), 3.U(width.W), 2.U(width.W), 2.U(width.W), 4.U(width.W)),
      Vector(2.U(width.W), 3.U(width.W), 3.U(width.W), 1.U(width.W), 3.U(width.W), 1.U(width.W), 3.U(width.W), 3.U(width.W), 3.U(width.W), 2.U(width.W)),
      Vector(1.U(width.W), 1.U(width.W), 4.U(width.W), 4.U(width.W), 1.U(width.W), 3.U(width.W), 4.U(width.W), 2.U(width.W), 3.U(width.W), 4.U(width.W)),
      Vector(1.U(width.W), 3.U(width.W), 1.U(width.W), 1.U(width.W), 2.U(width.W), 3.U(width.W), 2.U(width.W), 4.U(width.W), 2.U(width.W), 4.U(width.W)),
      Vector(4.U(width.W), 2.U(width.W), 3.U(width.W), 4.U(width.W), 1.U(width.W), 4.U(width.W), 1.U(width.W), 4.U(width.W), 4.U(width.W), 3.U(width.W)),
      Vector(3.U(width.W), 2.U(width.W), 1.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W), 3.U(width.W), 3.U(width.W), 4.U(width.W), 3.U(width.W)),
      Vector(3.U(width.W), 1.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W), 3.U(width.W), 1.U(width.W), 1.U(width.W), 3.U(width.W), 2.U(width.W)),
      Vector(4.U(width.W), 2.U(width.W), 4.U(width.W), 4.U(width.W), 3.U(width.W), 1.U(width.W), 2.U(width.W), 2.U(width.W), 3.U(width.W), 1.U(width.W)),
      Vector(1.U(width.W), 4.U(width.W), 1.U(width.W), 1.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W), 3.U(width.W), 3.U(width.W), 3.U(width.W)),
    )

    val weightMatrix: Vector[Vector[UInt]] = Vector(
      Vector(4.U(width.W), 3.U(width.W), 4.U(width.W), 1.U(width.W), 3.U(width.W), 3.U(width.W), 4.U(width.W), 1.U(width.W), 3.U(width.W), 1.U(width.W)),
      Vector(4.U(width.W), 4.U(width.W), 3.U(width.W), 2.U(width.W), 4.U(width.W), 1.U(width.W), 4.U(width.W), 1.U(width.W), 1.U(width.W), 4.U(width.W)),
      Vector(1.U(width.W), 1.U(width.W), 2.U(width.W), 4.U(width.W), 4.U(width.W), 3.U(width.W), 1.U(width.W), 3.U(width.W), 4.U(width.W), 4.U(width.W)),
      Vector(3.U(width.W), 3.U(width.W), 4.U(width.W), 1.U(width.W), 1.U(width.W), 2.U(width.W), 3.U(width.W), 2.U(width.W), 1.U(width.W), 2.U(width.W)),
      Vector(2.U(width.W), 1.U(width.W), 2.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W), 4.U(width.W), 1.U(width.W), 2.U(width.W), 3.U(width.W)),
      Vector(1.U(width.W), 3.U(width.W), 2.U(width.W), 3.U(width.W), 1.U(width.W), 2.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W), 2.U(width.W)),
      Vector(1.U(width.W), 1.U(width.W), 3.U(width.W), 2.U(width.W), 2.U(width.W), 2.U(width.W), 1.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W)),
      Vector(3.U(width.W), 4.U(width.W), 3.U(width.W), 2.U(width.W), 4.U(width.W), 3.U(width.W), 1.U(width.W), 3.U(width.W), 1.U(width.W), 1.U(width.W)),
      Vector(4.U(width.W), 4.U(width.W), 1.U(width.W), 4.U(width.W), 4.U(width.W), 3.U(width.W), 3.U(width.W), 2.U(width.W), 3.U(width.W), 2.U(width.W)),
      Vector(4.U(width.W), 2.U(width.W), 2.U(width.W), 4.U(width.W), 1.U(width.W), 2.U(width.W), 1.U(width.W), 1.U(width.W), 1.U(width.W), 3.U(width.W)),
    )

    val outputMatrix: Vector[Vector[UInt]] = Vector(
      Vector(49.U(width.W), 53.U(width.W), 56.U(width.W), 60.U(width.W), 57.U(width.W), 52.U(width.W), 59.U(width.W), 52.U(width.W), 49.U(width.W), 55.U(width.W)),
      Vector(62.U(width.W), 51.U(width.W), 56.U(width.W), 63.U(width.W), 57.U(width.W), 55.U(width.W), 55.U(width.W), 42.U(width.W), 47.U(width.W), 54.U(width.W)),
      Vector(65.U(width.W), 61.U(width.W), 60.U(width.W), 68.U(width.W), 73.U(width.W), 59.U(width.W), 59.U(width.W), 51.U(width.W), 54.U(width.W), 63.U(width.W)),
      Vector(67.U(width.W), 65.U(width.W), 68.U(width.W), 76.U(width.W), 65.U(width.W), 64.U(width.W), 59.U(width.W), 64.U(width.W), 62.U(width.W), 70.U(width.W)),
      Vector(65.U(width.W), 64.U(width.W), 57.U(width.W), 65.U(width.W), 61.U(width.W), 53.U(width.W), 56.U(width.W), 48.U(width.W), 44.U(width.W), 57.U(width.W)),
      Vector(86.U(width.W), 87.U(width.W), 79.U(width.W), 78.U(width.W), 80.U(width.W), 74.U(width.W), 79.U(width.W), 63.U(width.W), 66.U(width.W), 67.U(width.W)),
      Vector(82.U(width.W), 79.U(width.W), 76.U(width.W), 76.U(width.W), 74.U(width.W), 70.U(width.W), 78.U(width.W), 60.U(width.W), 61.U(width.W), 67.U(width.W)),
      Vector(62.U(width.W), 59.U(width.W), 60.U(width.W), 69.U(width.W), 64.U(width.W), 61.U(width.W), 66.U(width.W), 49.U(width.W), 59.U(width.W), 60.U(width.W)),
      Vector(71.U(width.W), 66.U(width.W), 71.U(width.W), 67.U(width.W), 75.U(width.W), 66.U(width.W), 70.U(width.W), 53.U(width.W), 61.U(width.W), 64.U(width.W)),
      Vector(71.U(width.W), 69.U(width.W), 63.U(width.W), 75.U(width.W), 72.U(width.W), 60.U(width.W), 70.U(width.W), 53.U(width.W), 53.U(width.W), 68.U(width.W)),
    )

    var inputRow = 0
    var inputCol = 0
    var weightRow = 0
    var weightCol = 0
    var outputRow = 0
    var outputCol = 0


    simulate(new SystolicArray(vectorSize, width)) { instance =>

      //result 2사분면
      matrixmultiply(inputRow=0, inputCol=0, weightRow=0, weightCol=0, outputRow=0, outputCol=0)
      println("result1 완료")
      //result 1사분면
      matrixmultiply(inputRow=0, inputCol=0, weightRow=0, weightCol=5, outputRow=0, outputCol=5)
      println("result2 완료")
      //result 3사분면
      matrixmultiply(inputRow=5, inputCol=0, weightRow=0, weightCol=0, outputRow=5, outputCol=0)
      println("result3 완료")
      //result 4사분면
      matrixmultiply(inputRow=5, inputCol=0, weightRow=0, weightCol=5, outputRow=5, outputCol=5)
      println("result4 완료")

      //5x5 결과를 내는 5x10 * 10x5 matrix multiply
      def matrixmultiply(inputRow:Int, inputCol: Int, weightRow: Int, weightCol: Int, outputRow:Int, outputCol:Int): Unit = {

        /*****첫 번째 matrix 연산*****/

        //clock #0
        println("clock 0")
        initialize(0, vectorSize)
        dataFeeding(0, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #1
        println("clock 1")
        initialize(5, vectorSize)
        dataFeeding(1, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #2
        println("clock 2")
        initialize(10, vectorSize)
        dataFeeding(2, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #3
        println("clock 3")
        initialize(15, vectorSize)
        dataFeeding(3, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #4
        println("clock 4")
        initialize(20, vectorSize)
        dataFeeding(4, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #5
        println("clock 5")
        initialize(25, vectorSize)
        dataFeeding(5, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #6
        println("clock 6")
        initialize(30, vectorSize)
        dataFeeding(6, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #7
        println("clock 7") //0
        initialize(35, vectorSize)
        dataFeeding(7, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        //clock #8
        println("clock 8") //0
        initialize(40, vectorSize)
        dataFeeding(8, inputRow, inputCol, weightRow, weightCol)
        instance.clock.step()

        /*****2번째 matrix 연산****/


        //clock #9
        println("clock 9") //0
        initialize(45, vectorSize)
        dataFeeding(0, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()

        //clock #10
        println("clock 10") //3
        dataFeeding(1, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()


        //clock #11
        println("clock 11") //15
        dataFeeding(2, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()


        //clock #12
        println("clock 12") //19
        dataFeeding(3, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()

        //clock #13
        println("clock 13")
        dataFeeding(4, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()

        //clock #14
        println("clock 14")
        dataFeeding(5, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()

        /*----------OUTPUT 시작-----------*/
        //clock #15
        println("clock 15")
        //output 확인
        instance.io.outputVector(0).expect(outputMatrix(outputRow)(outputCol))
        dataFeeding(6, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()

        //clock #16
        //output 확인
        instance.io.outputVector(1).expect(outputMatrix(outputRow + 1)(outputCol))
        //previous input 전달
        instance.io.controlVector(5).poke(false.B)

        dataFeeding(7, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()

        //clock #17
        //output  확인
        instance.io.outputVector(1).expect(outputMatrix(outputRow)(outputCol + 1))
        instance.io.outputVector(2).expect(outputMatrix(outputRow + 2)(outputCol))
        //previous input 전달
        instance.io.controlVector(6).poke(false.B)
        instance.io.controlVector(10).poke(false.B)

        dataFeeding(8, inputRow, inputCol + 5, weightRow + 5, weightCol)
        instance.clock.step()

        //clock #18
        println("clock 18")
        //output 확인
        instance.io.outputVector(2).expect(outputMatrix(outputRow + 1)(outputCol + 1))
        instance.io.outputVector(3).expect(outputMatrix(outputRow + 3)(outputCol))
        //previous input 전달
        instance.io.controlVector(7).poke(false.B)
        instance.io.controlVector(11).poke(false.B)
        instance.io.controlVector(15).poke(false.B)

        instance.clock.step()


        //clock #19
        println("clock 19")
        //output 확인
        instance.io.outputVector(2).expect(outputMatrix(outputRow)(outputCol + 2))
        instance.io.outputVector(3).expect(outputMatrix(outputRow + 2)(outputCol + 1))
        instance.io.outputVector(4).expect(outputMatrix(outputRow + 4)(outputCol + 0))
        //previous input 전달
        instance.io.controlVector(8).poke(false.B)
        instance.io.controlVector(12).poke(false.B)
        instance.io.controlVector(16).poke(false.B)
        instance.io.controlVector(20).poke(false.B)

        instance.clock.step()

        //clock #20
        println("clock 20")
        //output 확인
        instance.io.outputVector(3).expect(outputMatrix(outputRow + 1)(outputCol + 2))
        instance.io.outputVector(4).expect(outputMatrix(outputRow + 3)(outputCol + 1))
        instance.io.outputVector(5).expect(outputMatrix(outputRow + 4)(outputCol + 1))
        //previous input 전달
        instance.io.controlVector(13).poke(false.B)
        instance.io.controlVector(17).poke(false.B)
        instance.io.controlVector(21).poke(false.B)

        instance.clock.step()

        //clock #21
        //output 확인
        instance.io.outputVector(3).expect(outputMatrix(outputRow)(outputCol + 3))
        instance.io.outputVector(4).expect(outputMatrix(outputRow + 2)(outputCol + 2))
        instance.io.outputVector(5).expect(outputMatrix(outputRow + 3)(outputCol + 2))
        instance.io.outputVector(6).expect(outputMatrix(outputRow + 4)(outputCol + 2))
        //previous input 전달
        instance.io.controlVector(18).poke(false.B)
        instance.io.controlVector(22).poke(false.B)

        instance.clock.step()


        //clock #22
        println("clock 22")
        //output 확인
        instance.io.outputVector(4).expect(outputMatrix(outputRow + 1)(outputCol + 3))
        instance.io.outputVector(5).expect(outputMatrix(outputRow + 2)(outputCol + 3))
        instance.io.outputVector(6).expect(outputMatrix(outputRow + 3)(outputCol + 3))
        instance.io.outputVector(7).expect(outputMatrix(outputRow + 4)(outputCol + 3))
        //previous input 전달
        instance.io.controlVector(23).poke(false.B)
        instance.clock.step()

        //clock #23
        println("clock 23")
        //output 확인
        instance.io.outputVector(4).expect(outputMatrix(outputRow)(outputCol + 4))
        instance.io.outputVector(5).expect(outputMatrix(outputRow + 1)(outputCol + 4))
        instance.io.outputVector(6).expect(outputMatrix(outputRow + 2)(outputCol + 4))
        instance.io.outputVector(7).expect(outputMatrix(outputRow + 3)(outputCol + 4))
        instance.io.outputVector(8).expect(outputMatrix(outputRow + 4)(outputCol + 4))

        instance.clock.step()
      }

      def initialize(pos: Int, vectorSize: Int): Unit = {
        for(i <- 0 until vectorSize) {
          if((pos - i * 4) >= 0 && (pos - i * 4) < vectorSize * vectorSize) {
            instance.io.resetVector(pos - i * 4).poke(true.B)
            instance.io.controlVector(pos - i * 4).poke(true.B)
          }
          if((pos - 5 - i * 4) >= 0 && (pos - 5 - i * 4) < vectorSize * vectorSize)
            instance.io.resetVector(pos - 5 - i * 4).poke(false.B)
        }
      }

      def resetClear(vectorSize: Int): Unit = {
        for(i <- 0 until vectorSize * vectorSize)
          instance.io.resetVector(i).poke(false.B)
      }

      def dataFeeding(startIdx: Int, inputRow: Int, inputCol: Int, weightRow : Int, weightCol: Int): Unit = {
        for(i <- 0 until vectorSize) {
          var row = inputRow + i
          var col = inputCol - i + startIdx

          if (row >= inputRow && row < (inputRow + vectorSize) && col >= inputCol && col < (inputCol + vectorSize)) {
            instance.io.inputVector(i).poke(inputMatrix(row)(col))
//            print(inputMatrix(row)(col), " ")
          }
          else {
            instance.io.inputVector(i).poke(0.U(width.W))
//            print(s"0 ")
          }

          row =  weightRow - i + startIdx
          col = weightCol + i
          if (row >= weightRow && row < (weightRow + vectorSize) && col >= weightCol && col < (weightCol + vectorSize)) {
            instance.io.weightVector(i).poke(weightMatrix(row)(col))
          }
          else {
            instance.io.weightVector(i).poke(0.U(width.W))
          }
        }

//        println()
      }
    }
  }
}

/*
class ProcessingEngineTest extends AnyFreeSpec with Matchers {

  "check PE behavior" in {
    val width = 32
    val inputVector = Vector(1.U(width.W), 2.U(width.W), 3.U(width.W))
    val weightVector = Vector(1.U(width.W), 2.U(width.W), 3.U(width.W))

    simulate(new ProcessingEngine(width=width)) { c =>
      c.io.control.poke(false.B)
      c.io.reset.poke(false.B)
      c.io.previousResult.poke(0.U(width.W))


      for(i <- 0 until 3) {
        c.io.input.poke(inputVector(i))
        c.io.weight.poke(weightVector(i))
        c.clock.step()
      }
      c.io.output.expect(0.U(width.W))

      c.io.control.poke(true.B) //current data flow
      c.io.reset.poke(true.B)//reset

      c.clock.step()
      c.io.output.expect(14.U(width.W))
      c.clock.step()
      c.io.output.expect(9.U(width.W))



    }
  }
}

*/