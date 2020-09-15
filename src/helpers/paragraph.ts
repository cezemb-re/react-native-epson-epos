export function wrapText(str: string, width: number): string {
  return str.replace(
    new RegExp(`(?![^\\n]{1,${width}}$)([^\\n]{1,${width}})\\s`, 'g'),
    '$1\n'
  );
}

export function wrapTextToArray(str: string, width: number) {
  return wrapText(str, width).split('\n');
}
