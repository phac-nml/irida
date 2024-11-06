/*
 * This file is used to parse a file into tabular format
 */

/**
 * Parse CSV line into cell values array.
 *
 * Try to take into account the potential for value quoting and that commas can
 * be present within quoted values.
 *
 * @param line A line from a comma-separated values file.
 * @returns {Array} Cell values from the CSV line.
 */
function parseCsvLine(line) {
  function* gen(x) {
    yield* x;
  }
  const genLine = gen(line);
  const cells = [];
  let currCell = "";
  let isInQuote = false;
  let lastValue = null;
  while (true) {
    const next = genLine.next();
    if (next.done) {
      cells.push(currCell);
      break;
    }
    const { value } = next;
    switch (value) {
      case ",":
        if (isInQuote) {
          currCell += value;
          break;
        }
        cells.push(currCell);
        currCell = "";
        break;
      case '"':
        if (isInQuote && lastValue === "\\") {
          currCell += value;
          break;
        }
        if (lastValue === ",") {
          isInQuote = true;
          break;
        }
        isInQuote = false;
        break;
      default:
        currCell += value;
    }
    lastValue = value;
  }
  return cells;
}

function parseTabDelimitedLine(line) {
  return line.split("\t");
}

/**
 * Get basic table column definitions from a tab-delimited string
 * @param {string} firstLine Tab-delimited first line of an AnalysisOutputFile
 * @param {boolean} isCSV Is CSV format? Assume tab-delimited if false.
 * @returns {Array<Object<string>>} Basic table column definitions
 */
export function parseHeader(firstLine, isCSV = false) {
  let headers = [{ title: "#", dataIndex: "index" }];
  const firstRow = isCSV
    ? parseCsvLine(firstLine)
    : parseTabDelimitedLine(firstLine);
  for (let i = 0; i < firstRow.length; i++) {
    const col = firstRow[i];
    headers.push({
      title: col,
      dataIndex: i,
      key: col,
      scrollX: true,
    });
  }
  return headers;
}

/**
 * Split each line on tab characters.
 * @param {Array<string>} lines Tab-delimited lines
 * @param {number} offset Index offset
 * @param {boolean} isCSV Is CSV formatted line? Assume tab-delimited if false.
 * @returns {Array<Object<string>>}
 */
export function parseRows(lines, offset = 0, isCSV = false) {
  const rows = [];
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const cells = isCSV ? parseCsvLine(line) : parseTabDelimitedLine(line);
    const row = { index: offset + i + 1 };
    row["key"] = i + 1;
    row["index"] = i + 1;
    for (let j = 0; j < cells.length; j++) {
      row[j + ""] = cells[j];
    }
    rows.push(row);
  }

  return rows;
}

export function autoSizeAll({ columnApi }) {
  const allColumnIds = [];
  columnApi
    .getAllColumns()
    .forEach((column) => allColumnIds.push(column.colId));
  columnApi.autoSizeColumns(allColumnIds);
}
