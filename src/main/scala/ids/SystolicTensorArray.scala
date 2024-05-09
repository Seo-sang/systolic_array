package ids

import chisel3._

class SystolicTensorArray(blockSize: Int, PESize: Int, vectorSize: Int, width: Int) extends Module {

  val io = IO(new Bundle {
    val resetVector: Vec[UInt] = Input(Vec(blockSize * blockSize , Bool()))
    val controlVector: Vec[UInt] = Input(Vec(blockSize * blockSize , Bool()))
    val inputVector: Vec[UInt] = Input(Vec(blockSize * PESize * vectorSize, UInt(width.W)))
    val weightVector: Vec[UInt] = Input(Vec(blockSize * PESize * vectorSize, UInt(width.W)))
    val inputOutVector: Vec[UInt] = Output(Vec(blockSize * PESize * vectorSize, UInt(width.W)))
    val weightOutVector: Vec[UInt] = Output(Vec(blockSize * PESize * vectorSize, UInt(width.W)))
    val outputVector: Vec[UInt] = Output(Vec((blockSize * 2 - 1) * PESize * vectorSize, UInt(width.W)))
  })

  val blocks= Vector.fill(blockSize * blockSize)(Module(new BlockPE(PESize, vectorSize, width)))

  //input & weight pass through
  for(b <- 0 until blockSize) {
    for(cnt <- 0 until PESize * vectorSize) {
      io.inputOutVector(b * PESize * vectorSize + cnt) := blocks(b * blockSize + 1).io.inputOutputVector(cnt)
      io.weightOutVector(b * PESize * vectorSize + cnt) := blocks(blockSize * (blockSize - 1) + b).io.weightOutputVector(cnt)
    }
  }

  //reset & control
  for(row <- 0 until blockSize) {
    for(col <- 0 until blockSize) {
      val idx = row * blockSize + col
      blocks(idx).io.resetVector := io.resetVector(idx)
      blocks(idx).io.controlVector := io.controlVector(idx)
    }
  }

  for(row <- 0 until blockSize) {
    for(col <- 0 until blockSize) {
      val currentIdx = row * blockSize + col
      val leftIdx = row * blockSize + col - 1
      val aboveIdx = (row - 1) * blockSize + col
      val diagonalIdx = (row - 1) * blockSize + col + 1 //오른족 위

      //weight flow
      if(row == 0) {
        for(cnt <- 0 until PESize * vectorSize) {
          blocks(currentIdx).io.weightVector(cnt) := io.weightVector(col * PESize * vectorSize  + cnt)
        }
      }
      else {
        for(cnt <- 0 until PESize * vectorSize) {
          blocks(currentIdx).io.weightVector(cnt) := blocks(aboveIdx).io.weightOutputVector(cnt)
        }
      }

      //input flow
      if(col == 0) {
        for(cnt <- 0 until PESize * vectorSize) {
          blocks(currentIdx).io.inputVector(cnt) := io.inputVector(row * PESize * vectorSize + cnt)
        }
      }
      else {
        for(cnt <- 0 until PESize * vectorSize) {
          blocks(currentIdx).io.inputVector(cnt) := blocks(leftIdx).io.inputOutputVector(cnt)
        }
      }

      //output flow
      if(row == 0) {
        for(cnt <- 0 until PESize * PESize) {
          io.outputVector(col * PESize * PESize + cnt) := blocks(currentIdx).io.outputVector(cnt)
        }
      }
      else if(col == blockSize - 1) {
        for(cnt <- 0 until PESize * PESize) {
          io.outputVector((blockSize - 1 + row) * PESize * PESize + cnt) := blocks(currentIdx).io.outputVector(cnt)
        }
      }
      else {
        for(cnt <- 0 until PESize * PESize) {
          blocks(diagonalIdx).io.previousResultVector(cnt) := blocks(currentIdx).io.outputVector(cnt)
        }
      }

      if(col == 0 || row == blockSize - 1) {
        for(cnt <- 0 until PESize * PESize) {
          blocks(currentIdx).io.previousResultVector(cnt) := 0.U(width.W)
        }
      }
    }
  }
}

class BlockPE(PESize: Int, vectorSize: Int, width: Int) extends Module {

  val io = IO(new Bundle {
    val resetVector: Bool = Input(Bool())
    val controlVector: Bool = Input(Bool())
    val inputVector: Vec[UInt] = Input(Vec(PESize * vectorSize, UInt(width.W)))
    val weightVector: Vec[UInt] = Input(Vec(PESize * vectorSize, UInt(width.W)))
    val previousResultVector: Vec[UInt] = Input(Vec(PESize * PESize, UInt(width.W)))
    val outputVector: Vec[UInt] = Output(Vec(PESize * PESize, UInt(width.W)))
    val inputOutputVector : Vec[UInt] = Output(Vec(PESize * vectorSize, UInt(width.W)))
    val weightOutputVector : Vec[UInt] = Output(Vec(PESize * vectorSize, UInt(width.W)))
  })

  val PEs= Vector.fill(PESize * PESize)(Module(new TensorProcessingEngine(vectorSize, width)))
  val weightVectorReg = RegInit(VecInit(Seq.fill(PESize * vectorSize)(0.U(width.W))))
  val inputVectorReg = RegInit(VecInit(Seq.fill(PESize * vectorSize)(0.U(width.W))))

  for(i <- 0 until PESize) {
    for(j <- 0 until vectorSize) {
      val idx = i * vectorSize + j
      inputVectorReg(idx) := PEs(i * PESize + (PESize - 1)).io.inputOut(j)
      weightVectorReg(idx) := PEs(PESize + i).io.weightOut(j)

      io.inputOutputVector(idx) := inputVectorReg(idx)
      io.weightOutputVector(idx) := weightVectorReg(idx)
    }
  }

  for(row <- 0 until PESize) {
    for(col <- 0 until PESize) {
      val currentIdx = row * PESize + col
      val leftIdx = row * PESize + (col - 1)
      val aboveIdx = (row - 1) * PESize + col

      //weight flow
      if(row == 0) {
        for(cnt <- 0 until vectorSize) {
          PEs(currentIdx).io.weight(cnt) := io.weightVector(col * vectorSize + cnt)
        }
      }
      else {
        for(cnt <- 0 until vectorSize) {
          PEs(currentIdx).io.weight(cnt) := PEs(aboveIdx).io.weightOut(cnt)
        }
      }

      //input flow
      if(col == 0) {
        for(cnt <- 0 until vectorSize) {
          PEs(currentIdx).io.input(cnt) := io.inputVector(row * vectorSize + cnt)
        }
      }
      else {
        for(cnt <- 0 until vectorSize) {
          PEs(currentIdx).io.input(cnt) := PEs(leftIdx).io.inputOut(cnt)
        }
      }

      //output flow
      io.outputVector(currentIdx) := PEs(currentIdx).io.output

      //reset & control flow
      PEs(currentIdx).io.reset := io.resetVector
      PEs(currentIdx).io.control := io.controlVector


      //previous result flow
      PEs(currentIdx).io.previousResult := io.previousResultVector(currentIdx)

    }
  }

  io.inputOutputVector := inputVectorReg
  io.weightOutputVector := weightVectorReg

}

class TensorProcessingEngine(vectorSize:Int = 2, width: Int) extends Module {
  val io = IO(new Bundle {
    val reset = Input(Bool())
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
  val candidate: UInt = Wire(UInt(width.W))

  //output
  io.weightOut := io.weight
  io.inputOut := io.input

  candidate := Mux(io.reset, 0.U(width.W), partialSum)

  //partial sum calculation
  partialSum := (io.input(0) * io.weight(0)) + (io.input(1) * io.weight(1)) + candidate

  //select output
  outputReg := Mux(io.control, partialSum, io.previousResult)
  io.output := outputReg
}
