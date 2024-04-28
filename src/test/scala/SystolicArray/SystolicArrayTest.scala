package SystolicArray

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.util.Random

class SystolicArrayTest extends AnyFreeSpec with Matchers {

  "check systolic array behavior" in {
    val vectorSize = 3
    val width = 16
    val inputVector: Vector[Vector[Int]] = Vector(Vector(1, 2, 3), Vector(4, 5, 6), Vector(7, 8, 9))
    val weightVector: Vector[Vector[Int]] = Vector(Vector(1, 2, 3), Vector(4, 5, 6), Vector(7, 8, 9))
    val outputVector = Vector(Vector(30, 36, 42), Vector(66, 81, 96), Vector(102, 126, 150))

    simulate(new SystolicArray(vectorSize, width)) { c =>
      for(clk <- 0 until 10) {

        //data align
        var input: Vector[UInt] = Vector()
        var weight: Vector[UInt] = Vector()

        for(i <- 0 until vectorSize) {
          val row = i
          val col = clk - i
          if(row >= 0 && row < vectorSize && col >= 0 && col < vectorSize) {
            input = input :+ inputVector(row)(col).asUInt(width)
            weight = weight :+ weightVector(col)(row).asUInt(width)
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
        for(row <- 0 until vectorSize)
          for(col <- 0 until vectorSize)
            c.io.outputVector(row)(col).expect(outputVector(row)(col))
      }

    }
  }
}
