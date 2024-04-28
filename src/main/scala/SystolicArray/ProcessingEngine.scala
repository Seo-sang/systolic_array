package SystolicArray

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._

class ProcessingEngine(width : Int) extends Module{

  val io = IO(new Bundle {
    val control = Input(Bool()) //1이면 current, 0이면 previous
    val input = Input(UInt(width.W))
    val weight = Input(UInt(width.W))
    val previousResult = Input(UInt(width.W))
    val weightOut = Output(UInt(width.W))
    val inputOut = Output(UInt(width.W))
    val output = Output(UInt(width.W))
  })

  val weightReg = RegNext(io.weight, 0.U)
  val inputReg = RegNext(io.input, 0.U)
  val partialSum = RegInit(UInt(width.W), 0.U)
  val outputReg = RegNext(partialSum, 0.U)

  //output
  io.weightOut := weightReg
  io.inputOut := inputReg

  //partial sum calculation
  partialSum := (io.input * io.weight) + partialSum

  //select output
  outputReg := Mux(io.control, partialSum, io.previousResult)
  io.output := outputReg

}
