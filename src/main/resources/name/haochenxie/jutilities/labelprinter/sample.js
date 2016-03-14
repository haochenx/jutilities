/**
 * translate mm to points
 */
function translate(x) {
  return x * 2.83464567;
}

function drawLabel(area, g) {
  var margin = translate(4);
  g.translate(margin, margin);
  g.setColor(java.awt.Color.black);

  var font1 = new java.awt.Font("Savoye LET", 2, 16);
  var font2 = new java.awt.Font("Skia", 0, 12);
  var font3 = new java.awt.Font("Skia", 0, 10);

  g.setFont(font1);
  g.drawString("Utility brought to you by", -9, 8);

  g.setFont(font2);
  g.drawString("Haochen Xie", 8, 24);
  g.drawLine(8, 27, 75, 27);

  g.setFont(font3);
  g.drawString("haochenx@acm.org", -3, 40);
}
