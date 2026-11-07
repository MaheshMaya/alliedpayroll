import { readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

import { parse, TYPE } from "@formatjs/icu-messageformat-parser";
import { printAST } from "@formatjs/icu-messageformat-parser/printer.js";

const messagesDir = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  "../src/shared/i18n/messages",
);
const sourceFile = path.join(messagesDir, "en-IN.json");
const targetFile = path.join(messagesDir, "qps-ploc.json");

const ACCENTED = new Map(
  Object.entries({
    a: "á",
    b: "ƀ",
    c: "ç",
    d: "ð",
    e: "é",
    f: "ƒ",
    g: "ĝ",
    h: "ĥ",
    i: "í",
    j: "ĵ",
    k: "ķ",
    l: "ĺ",
    m: "ɱ",
    n: "ñ",
    o: "ó",
    p: "þ",
    r: "ŕ",
    s: "š",
    t: "ţ",
    u: "ú",
    v: "ṽ",
    w: "ŵ",
    x: "ẋ",
    y: "ý",
    z: "ž",
    A: "Á",
    B: "Ɓ",
    C: "Ç",
    D: "Ð",
    E: "É",
    F: "Ƒ",
    G: "Ĝ",
    H: "Ĥ",
    I: "Í",
    J: "Ĵ",
    K: "Ķ",
    L: "Ĺ",
    M: "Ḿ",
    N: "Ñ",
    O: "Ó",
    P: "Þ",
    R: "Ŕ",
    S: "Š",
    T: "Ţ",
    U: "Ú",
    V: "Ṽ",
    W: "Ŵ",
    X: "Ẋ",
    Y: "Ý",
    Z: "Ž",
  }),
);

/** @param {string} text */
const accent = (text) =>
  Array.from(text, (char) => ACCENTED.get(char) ?? char).join("");

/**
 * Accents literal text only; arguments, plural and select syntax stay intact so ICU still parses.
 * @param {import('@formatjs/icu-messageformat-parser').MessageFormatElement[]} elements
 */
function pseudoLocalise(elements) {
  for (const element of elements) {
    if (element.type === TYPE.literal) {
      element.value = accent(element.value);
    } else if (element.type === TYPE.plural || element.type === TYPE.select) {
      for (const option of Object.values(element.options))
        pseudoLocalise(option.value);
    } else if (element.type === TYPE.tag) {
      pseudoLocalise(element.children);
    }
  }
}

/** @param {string} message */
function pseudo(message) {
  const ast = parse(message);
  pseudoLocalise(ast);
  // Roughly 40% longer, like German or Tamil, so clipping shows up before translation does.
  const padding = "~".repeat(Math.ceil(message.length * 0.4));
  // eslint-disable-next-line @typescript-eslint/no-unsafe-enum-assignment -- parse() and printAST() share TYPE; the rule cannot see across the package's two entry points.
  return `[${printAST(ast)}${padding}]`;
}

/**
 * @param {string} text
 * @returns {Record<string, string>}
 */
function readMessages(text) {
  const data = /** @type {unknown} */ (JSON.parse(text));
  if (typeof data !== "object" || data === null)
    throw new Error(`${sourceFile} must hold a JSON object`);
  /** @type {Record<string, string>} */
  const messages = {};
  for (const [key, value] of Object.entries(data)) {
    if (typeof value !== "string")
      throw new Error(`${sourceFile}: "${key}" is not a string`);
    messages[key] = value;
  }
  return messages;
}

const source = readMessages(await readFile(sourceFile, "utf8"));
const output = Object.fromEntries(
  Object.entries(source).map(([key, message]) => [key, pseudo(message)]),
);
await writeFile(targetFile, `${JSON.stringify(output, null, 2)}\n`, "utf8");
console.log(
  `i18n: wrote ${String(Object.keys(output).length)} pseudo-localised messages to ${path.relative(process.cwd(), targetFile)}`,
);
