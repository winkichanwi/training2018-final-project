export class TicketReserve {
  constructor(
    public restaurant_id: number,
    public seat_no: number
  ) {
  }
}

export interface IRestaurantTicketCountResponse {
  restaurant_id: number;
  ticket_counts: IRestaurantTicketCount[];
}

export interface IRestaurantTicketCount {
  ticket_type: string;
  count: number;
}

export interface IRestaurantLastCalled {
  ticket_type: string;
  last_called: number;
}

export interface IReservedTicket {
  ticket_id: number;
  restaurant_id: number;
  ticket_type: string;
  seat_no: number;
  ticket_no: number;
}

export const TICKET_STATUS_ACCEPTED = 'Accepted';
export const TICKET_STATUS_CANCELLED = 'Cancelled';

export const TICKET_TYPES = [
  {type: 'A', min_seat_no: 1, max_seat_no: 2},
  {type: 'B', min_seat_no: 3, max_seat_no: 5},
  {type: 'C', min_seat_no: 6, max_seat_no: 8},
  {type: 'D', min_seat_no: 9, max_seat_no: 12},
];
