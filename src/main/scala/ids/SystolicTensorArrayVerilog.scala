package ids

import _root_.circt.stage.ChiselStage

object SystolicTensorArrayVerilog extends App {
  ChiselStage.emitSystemVerilogFile(new SystolicTensorArray(2, 2, 2, 32),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}