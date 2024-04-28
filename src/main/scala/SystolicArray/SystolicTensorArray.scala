package SystolicArray

import chisel3._

class SystolicTensorArray(blockSize: Int, PESize: Int, vectorSize: Int, width: Int) {

  val io = IO(new Bundle {
    val inputVector: Vec[UInt] = Input(Vec(blockSize * PESize * vectorSize, UInt(width.W)))
    val weightVector: Vec[UInt] = Input(Vec(blockSize * PESize * vectorSize, UInt(width.W)))
    val outputVector: Vec[UInt] = Output(Vec(blockSize * PESize * vectorSize, UInt(width.W)))
  })

  val blocks= Vector.fill(blockSize * blockSize)(Module(new BlockPE(PESize, vectorSize, width)))

  for(row <- 0 until blockSize) {
    for(col <- 0 until blockSize) {
      val currentIdx = row * blockSize + col
      val rightIdx = row * blockSize + col + 1
      val belowIdx = (row + 1) * blockSize + col

      if(row == 0) {
        for(cnt <- 0 until PESize * vectorSize)
          blocks(currentIdx).io.weightVector(cnt) := io.weightVector(col * blockSize + cnt)
      }
      else if(row != blockSize - 1) blocks(belowIdx).io.weightVector := blocks(currentIdx).io.weightOutputVector

      if(col == 0) {
        for(cnt <- 0 until PESize * vectorSize)
          blocks(currentIdx).io.inputVector(cnt) := io.inputVector(row * blockSize + cnt)
      }
      else if(col != blockSize - 1) blocks(rightIdx).io.inputVector := blocks(currentIdx).io.inputOutputVector
    }
  }
}

class BlockPE(PESize: Int, vectorSize: Int, width: Int) {

  val io = IO(new Bundle {
    val inputVector: Vec[UInt] = Input(Vec(PESize * vectorSize, UInt(width.W)))
    val weightVector: Vec[UInt] = Input(Vec(PESize * vectorSize, UInt(width.W)))
    val outputVector: Vec[UInt] = Output(Vec(PESize * PESize, UInt(width.W)))
    val inputOutputVector : Vec[UInt] = Output(Vec(PESize * vectorSize, UInt(width.W)))
    val weightOutputVector : Vec[UInt] = Output(Vec(PESize * vectorSize, UInt(width.W)))
  })
  val PEs= Vector.fill(PESize * PESize)(Module(new TensorProcessingEngine(vectorSize, width)))
  val weightVectorReg = RegInit(VecInit(Seq.fill(PESize * vectorSize)(0.U(width.W))))
  val inputVectorReg = RegInit(VecInit(Seq.fill(PESize * vectorSize)(0.U(width.W))))

  for(row <- 0 until PESize) {
    for(col <- 0 until PESize) {
      val currentIdx = row * PESize + col
      val rightIdx = row * PESize + (col + 1)
      val belowIdx = (row + 1) * PESize + col

      if(row == 0) {
        for(cnt <- 0 until vectorSize) {
          PEs(currentIdx).io.weight(cnt) := io.weightVector(col * vectorSize + cnt)
        }
      }
      else if(row != PESize - 1) PEs(belowIdx).io.weight := PEs(currentIdx).io.weightOut
      else weightVectorReg := PEs(currentIdx).io.weightOut

      if(col == 0) {
        for(cnt <- 0 until vectorSize) {
          PEs(currentIdx).io.input(cnt) := io.inputVector(row * vectorSize + cnt)
        }
      }
      else if(col != PESize - 1) PEs(rightIdx).io.input := PEs(currentIdx).io.inputOut
      else inputVectorReg := PEs(currentIdx).io.inputOut

      io.outputVector := PEs(currentIdx).io.output
    }
  }

  io.inputOutputVector := inputVectorReg
  io.weightOutputVector := weightVectorReg

}

class TensorProcessingEngine(vectorSize:Int, width: Int) {
  val io = IO(new Bundle {
    val control = Input(Bool()) //1이면 current, 0이면 previous
    val input = Input(Vec(vectorSize, UInt(width.W)))
    val weight = Input(Vec(vectorSize, UInt(width.W)))
    val previousResult = Input(UInt(width.W))
    val weightOut = Output(Vec(vectorSize, UInt(width.W)))
    val inputOut = Output(Vec(vectorSize, UInt(width.W)))
    val output = Output(UInt(width.W))
  })

  val partialSum = RegInit(UInt(width.W), 0.U)
  val outputReg = RegNext(partialSum, 0.U)

  //output
  io.weightOut := io.weight
  io.inputOut := io.input

  //partial sum calculation
  for(i <- 0 until vectorSize)
    partialSum := (io.input(i) * io.weight(i)) + partialSum

  //select output
  outputReg := Mux(io.control, partialSum, io.previousResult)
  io.output := outputReg
}
