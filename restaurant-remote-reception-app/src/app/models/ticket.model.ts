export class Ticket {
  constructor(
    public restaurant_id: number,
    public created_by_id: number,
    public seat_no: number,
    public ticket_status: string
  ) {
  }
}

export interface ITicketCurrentCount {
  ticket_type: string;
  ticket_count: string;
}

export interface ITicketLastNo {
  ticket_type: string;
  last_called: number;
  last_taken: number;
}

export const TICKET_TYPES = [
  {type: 'A', min_seat_no: 1, max_seat_no: 2},
  {type: 'B', min_seat_no: 3, max_seat_no: 5},
  {type: 'C', min_seat_no: 6, max_seat_no: 8},
  {type: 'D', min_seat_no: 9, max_seat_no: 12},
];
