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
