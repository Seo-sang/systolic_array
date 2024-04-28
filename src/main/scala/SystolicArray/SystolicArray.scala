package SystolicArray

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
//import _root_.circt.stage.ChiselStage

class SystolicArray(vectorSize : Int, width : Int) extends Module{


  val io = IO(new Bundle {
    val inputVector: Vec[UInt] = Input(Vec(vectorSize, UInt(width.W)))
    val weightVector: Vec[UInt] = Input(Vec(vectorSize, UInt(width.W)))
    //val outputVector: Vec[UInt] = Output(Vec(vectorSize * 2 - 1, UInt(width.W)))
    val outputVector: Vec[UInt] = Output(Vec(vectorSize * vectorSize, UInt(width.W)))
  })

  val PEs= Vector.fill(vectorSize * vectorSize)(Module(new ProcessingEngine(width)))
  val inputRegVector = RegInit(VecInit(Seq.fill(vectorSize)(0.U(width.W))))
  val weightRegVector =  RegInit(VecInit(Seq.fill(vectorSize)(0.U(width.W))))

  //input & weight buffer
  for(i <- 0 until vectorSize) {
    inputRegVector(i) := io.inputVector(i)
    weightRegVector(i) := io.weightVector(i)
  }


  //vertical & horizontal transfer
  for(row <- 0 until vectorSize - 1) {
    for(col <- 0 until vectorSize - 1) {
      val currentIdx = row * (vectorSize) + col
      val belowIdx = (row + 1) * (vectorSize) + col
      val rightIdx = row * (vectorSize) + col + 1

      PEs(rightIdx).io.input := PEs(currentIdx).io.inputOut
      PEs(belowIdx).io.weight := PEs(currentIdx).io.weightOut

    }
  }

  //output connection
  for(row <- 0 until vectorSize) {
    for(col <- 0 until vectorSize) {

      val idx = row * vectorSize + col
      /*
      val crossIdx = (row + 1) * vectorSize + (col - 1)

      if(col == 0) io.outputVector(col) := PEs(idx).io.output
      else if(row == vectorSize - 1) io.outputVector(vectorSize + col - 1) := PEs(idx).io.output
      else PEs(crossIdx).io.previousResult := PEs(idx).io.output
      */

      io.outputVector(idx) := PEs(idx).io.output
    }
  }
}