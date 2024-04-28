package SystolicArray

import chisel3.stage.ChiselStage

object SystolicArrayVerilog extends App {
  (new ChiselStage).emitVerilog(new SystolicArray(5, 32))
}
