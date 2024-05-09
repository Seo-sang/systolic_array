package ids

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._

class ProcessingEngine(width: Int) extends Module{

  val io = IO(new Bundle {
    val reset: Bool = Input(Bool())
    val control: Bool = Input(Bool()) //1이면 current result, 0이면 previous
    val input: UInt = Input(UInt(width.W))
    val weight: UInt = Input(UInt(width.W))
    val previousResult: UInt = Input(UInt(width.W))
    val weightOut: UInt = Output(UInt(width.W))
    val inputOut: UInt = Output(UInt(width.W))
    val output: UInt = Output(UInt(width.W))
  })

  val weightReg: UInt = RegNext(io.weight, 0.U(width.W))
  val inputReg: UInt = RegNext(io.input, 0.U(width.W))
  val partialSum: UInt = RegInit(UInt(width.W), 0.U(width.W))
  val outputReg: UInt = RegInit(UInt(width.W), 0.U(width.W))
  val candidate: UInt = Wire(UInt(width.W))

  //output
  io.weightOut := weightReg
  io.inputOut := inputReg

  candidate := Mux(io.reset, 0.U(width.W), partialSum)

  //partial sum calculation
  //partialSum := Mux(io.reset, (io.input * io.weight), (io.input * io.weight) + partialSum)
  partialSum := (io.input * io.weight) + candidate

  //select output
  outputReg := Mux(io.control, partialSum, io.previousResult)
  io.output := outputReg

}
