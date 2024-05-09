package ids

import _root_.circt.stage.ChiselStage

object SystolicArrayVerilog extends App {
  ChiselStage.emitSystemVerilogFile(new SystolicArray(5, 32),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}