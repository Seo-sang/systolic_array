package SystolicArray

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.util.Random

class SystolicArrayTest extends AnyFreeSpec with Matchers {

  "check systolic array behavior" in {
    val vectorSize = 3
    val width = 32
    //val inputVector: Vector[Vector[UInt]] = Vector(Vector(1.U, 2.U, 3.U), Vector(4.U, 5.U, 6.U), Vector(7.U, 8.U, 9.U))
    val inputVector: Vector[UInt] = Vector(1.U, 2.U, 3.U, 4.U, 5.U, 6.U)
    //val inputVector = Vector(Vector(1.U(width.W), 2.U(width.W), 3.U(width.W)), Vector(4.U(width.W), 5.U(width.W), 6.U(width.W)), Vector(7.U(width.W), 8.U(width.W), 9.U(width.W)))
    //val weightVector: Vector[Vector[UInt]] = Vector(Vector(1.U, 2.U, 3.U), Vector(4.U, 5.U, 6.U), Vector(7.U, 8.U, 9.U))
    //val weightVector = Vector(Vector(1.U(width.W), 2.U(width.W), 3.U(width.W)), Vector(4.U(width.W), 5.U(width.W), 6.U(width.W)), Vector(7.U(width.W), 8.U(width.W), 9.U(width.W)))
    val weightVector: Vector[UInt] = Vector(1.U, 2.U, 3.U, 4.U, 5.U, 6.U)
    val outputVector = Vector(Vector(30.U, 36.U, 42.U), Vector(66.U, 81.U, 96.U), Vector(102.U, 126.U, 150.U))

    simulate(new SystolicArray(vectorSize, width)) { c =>
      for(clk <- 0 until 10) {

        //data align
        var input: Vector[UInt] = Vector()
        var weight: Vector[UInt] = Vector()

        for(i <- 0 until vectorSize) {
          val row = i
          val col = clk - i
          if(row >= 0 && row < vectorSize && col >= 0 && col < vectorSize) {
            input = input :+ inputVector(row * vectorSize + col)
            weight = weight :+ weightVector(col * vectorSize + row)
          } else {
            input = input :+ 0.U(width.W)
            weight = weight :+ 0.U(width.W)
          }
        }

        for(i <- 0 until vectorSize) {
          c.io.inputVector(i).poke(input(i))
          c.io.weightVector(i).poke(weight(i))
        }
        c.clock.step()
        for(row <- 0 until vectorSize) {
          for(col <- 0 until vectorSize) {
            val idx = row * vectorSize + col
            c.io.outputVector(idx).expect(outputVector(row)(col))
          }
        }
      }
    }
  }
}
