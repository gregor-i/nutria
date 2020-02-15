// https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#q3i69d

Blockly.Blocks['complex_number'] = {
  init: function() {
    this.appendValueInput("REAL")
        .setCheck("Number")
        .appendField("real");
    this.appendValueInput("IMAG")
        .setCheck("Number")
        .appendField("imag");
    this.setOutput(true, "complex_number");
    this.setColour(75);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};