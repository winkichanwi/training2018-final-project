export class TicketReserve {
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
  ticket_count: number;
}

export interface ITicketLastNo {
  ticket_type: string;
  last_called: number;
  last_taken: number;
}

export interface IReservedTicket {
  ticket_id: number;
  restaurant_id: number;
  ticket_type: string;
  seat_no: number;
  ticket_no: number;
  last_called: number;
}
//
// export interface ITicketStatus {
//   status: string;
//   is_waiting: boolean;
// }
//
// export const TICKET_STATUS: ITicketStatus[] = [
//   {status: 'Active', is_waiting: true},
//   {status: 'Called', is_waiting: true},
//   {status: 'Cancelled', is_waiting: false}, // cancelled by either customer or restaurant
//   {status: 'Accepted', is_waiting: false}, // accepted by restaurant
//   {status: 'Archived', is_waiting: false}, // archive when restaurant closes
// ];

export const TICKET_TYPES = [
  {type: 'A', min_seat_no: 1, max_seat_no: 2},
  {type: 'B', min_seat_no: 3, max_seat_no: 5},
  {type: 'C', min_seat_no: 6, max_seat_no: 8},
  {type: 'D', min_seat_no: 9, max_seat_no: 12},
];
