package ids

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
//import _root_.circt.stage.ChiselStage

class SystolicArray(vectorSize : Int, width : Int) extends Module{

  val io = IO(new Bundle {
    val resetVector: Vec[Bool] = Input(Vec(vectorSize * vectorSize, Bool()))
    val controlVector: Vec[Bool] = Input(Vec(vectorSize * vectorSize, Bool()))
    val inputVector: Vec[UInt] = Input(Vec(vectorSize, UInt(width.W)))
    val weightVector: Vec[UInt] = Input(Vec(vectorSize, UInt(width.W)))
    val inputOutVector: Vec[UInt] = Output(Vec(vectorSize, UInt(width.W)))
    val weightOutVector: Vec[UInt] = Output(Vec(vectorSize, UInt(width.W)))
    val outputVector: Vec[UInt] = Output(Vec(vectorSize * 2 - 1, UInt(width.W)))
  })

  val PEs= Vector.fill(vectorSize * vectorSize)(Module(new ProcessingEngine(width)))

  //val inputRegVector = RegInit(VecInit(Seq.fill(vectorSize)(0.U(width.W))))
  //val weightRegVector =  RegInit(VecInit(Seq.fill(vectorSize)(0.U(width.W))))

  //input & weight buffer
  for(i <- 0 until vectorSize) {
    //inputRegVector(i) := io.inputVector(i)
    //weightRegVector(i) := io.weightVector(i)

    //input & weight out
    io.inputOutVector(i) := PEs((i * vectorSize) + vectorSize - 1).io.inputOut
    io.weightOutVector(i) := PEs(vectorSize * (vectorSize - 1) + i).io.weightOut

  }

  for(row <- 0 until vectorSize) {
    for(col <- 0 until vectorSize) {
      val idx = row * vectorSize + col
      PEs(idx).io.reset := io.resetVector(idx)
      PEs(idx).io.control := io.controlVector(idx)
    }
  }

  //vertical & horizontal transfer
  for(row <- 0 until vectorSize) {
    for(col <- 0 until vectorSize) {
      val currentIdx = row * (vectorSize) + col
      val leftIdx = row * vectorSize + col - 1
      val aboveIdx = (row - 1) * vectorSize + col

      //input flow
      if(col == 0) PEs(currentIdx).io.input := io.inputVector(row)
      else PEs(currentIdx).io.input := PEs(leftIdx).io.inputOut

      //weight flow
      if(row == 0) PEs(currentIdx).io.weight := io.weightVector(col)
      else PEs(currentIdx).io.weight := PEs(aboveIdx).io.weightOut
    }
  }

  //output connection
  for(row <- 0 until vectorSize) {
    for(col <- 0 until vectorSize) {
      val currentIdx = row * vectorSize + col
      if(col == 0) {
        io.outputVector(row) := PEs(currentIdx).io.output
      }
      else if(row == vectorSize - 1) {
        io.outputVector(vectorSize - 1 + col) := PEs(currentIdx).io.output
      }
      else { //diagonal connection
        val diagonalIdx = (row + 1) * vectorSize + col - 1

        PEs(diagonalIdx).io.previousResult := PEs(currentIdx).io.output
      }

      if(row == 0) PEs(currentIdx).io.previousResult := 0.U(width.W)
      else if(col == vectorSize - 1) PEs(currentIdx).io.previousResult := 0.U(width.W)
    }
  }
}
