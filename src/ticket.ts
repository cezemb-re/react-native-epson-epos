import type { Image } from './printer';
import {
  addCut,
  addImage,
  addText,
  addTextAlign,
  addTextFont,
  addTextSize,
  beginTransaction,
  clearCommandBuffer,
  endTransaction,
  Font,
  sendData,
  TextAlign,
} from './printer';
import { wrapTextToArray } from './helpers/paragraph';

export enum TicketParagraphType {
  TEXT = 'text',
  TABLE = 'table',
  IMAGE = 'image',
}

export interface TicketParagraph {
  type?: TicketParagraphType;
  align?: TextAlign;
  marginTop?: number;
  marginBottom?: number;
  [key: string]: any;
}

export interface TicketImageParagraph extends TicketParagraph {
  image: Image;
}

export type Ticket = Array<TicketParagraph>;

async function addParagraphMarginTop(
  paragraph: TicketParagraph
): Promise<void> {
  if (!('marginTop' in paragraph) || !paragraph.marginTop) {
    return;
  }
  await addText('\n'.repeat(paragraph.marginTop));
}

async function addParagraphMarginBottom(
  paragraph: TicketParagraph
): Promise<void> {
  if (!('marginBottom' in paragraph) || !paragraph.marginBottom) {
    return;
  }
  await addText('\n'.repeat(paragraph.marginBottom));
}

export interface TicketTextParagraph extends TicketParagraph {
  value: string;
  size?: number;
  font?: Font;
}

export interface TicketWrappedTextParagraph extends TicketTextParagraph {
  width: number;
  wrappedText: Array<string>;
}

export interface TicketTableParagraph extends TicketParagraph {
  cellSpace: number;
  columns: Array<TicketTextParagraph>;
}

async function addTextParagraph(
  textParagraph: TicketTextParagraph
): Promise<void> {
  if (!('size' in textParagraph) || !textParagraph.size) {
    textParagraph.size = 1;
  }
  await addTextSize(textParagraph.size, textParagraph.size);

  if (!('font' in textParagraph) || !textParagraph.font) {
    textParagraph.font = Font.A;
  }
  await addTextFont(textParagraph.font);

  await addText(textParagraph.value + '\n');
}

async function addTableParagraph(
  tableParagraph: TicketTableParagraph
): Promise<void> {
  if (!tableParagraph.columns || !tableParagraph.columns.length) {
    return;
  }

  let nbLines = 0;

  const wrappedColumns = <Array<TicketWrappedTextParagraph>>(
    tableParagraph.columns.map((column: TicketTextParagraph) => {
      if (!('width' in column) || !column.width) {
        column.width = 20;
      }

      column.wrappedText = wrapTextToArray(column.value, column.width);

      if (column.wrappedText.length > nbLines) {
        nbLines = column.wrappedText.length;
      }

      return column;
    })
  );

  for (let i = 0; i < nbLines; i++) {
    await Promise.all(
      wrappedColumns.map(
        async (wrappedColumn: TicketWrappedTextParagraph, index: number) => {
          let line = '';

          if (index && 'cellSpace' in tableParagraph) {
            line += ' '.repeat(tableParagraph.cellSpace);
          }

          if (
            wrappedColumn.wrappedText &&
            wrappedColumn.wrappedText.length > i
          ) {
            line +=
              wrappedColumn.wrappedText[i] +
              ' '.repeat(
                wrappedColumn.width - wrappedColumn.wrappedText[i].length
              );
          } else {
            line += ' '.repeat(wrappedColumn.width);
          }

          if (!('size' in wrappedColumn) || !wrappedColumn.size) {
            wrappedColumn.size = 1;
          }

          await addTextSize(wrappedColumn.size, wrappedColumn.size);

          if (!('font' in wrappedColumn) || !wrappedColumn.font) {
            wrappedColumn.font = Font.A;
          }
          await addTextFont(wrappedColumn.font);

          await addText(line);
        }
      )
    );

    await addText('\n');
  }

  // if (!('size' in textParagraph) || !textParagraph.size) {
  //   textParagraph.size = 1;
  // }
  // await addTextSize(textParagraph.size, textParagraph.size);
  //
  // if (!('font' in textParagraph) || !textParagraph.font) {
  //   textParagraph.font = Font.A;
  // }
  // await addTextFont(textParagraph.font);
  //
  // await addText(textParagraph.value + '\n');
}

export async function printTicket(
  ticket: Ticket,
  copy: number = 1
): Promise<void> {
  if (!ticket || !ticket.length) {
    return;
  }

  for (const paragraph of ticket) {
    await addParagraphMarginTop(paragraph);

    if (!('align' in paragraph)) {
      paragraph.align = TextAlign.LEFT;
    }
    await addTextAlign(paragraph.align);

    if (!('type' in paragraph)) {
      paragraph.type = TicketParagraphType.TEXT;
    }

    switch (paragraph.type) {
      case TicketParagraphType.TEXT:
        await addTextParagraph(<TicketTextParagraph>paragraph);
        break;

      case TicketParagraphType.TABLE:
        await addTableParagraph(<TicketTableParagraph>paragraph);
        break;

      case TicketParagraphType.IMAGE:
        await addImage((<TicketImageParagraph>paragraph).image);
        break;
    }

    await addParagraphMarginBottom(paragraph);
  }

  await addCut();
  await beginTransaction();

  for (let i = 0; i < copy; i++) {
    await sendData();
  }

  await endTransaction();
  await clearCommandBuffer();
}
