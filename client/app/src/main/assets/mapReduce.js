function map(doc) {
  if (doc.digit) {
    doc.digit.forEach(function(digit, index) {
      emit([index, digit.values[0]], 1);
    });
  }
}

function reduce(keys, values, rereduce) {
  if (rereduce) {
    return sum(values);
  } else {
    return values.length;
  }
}