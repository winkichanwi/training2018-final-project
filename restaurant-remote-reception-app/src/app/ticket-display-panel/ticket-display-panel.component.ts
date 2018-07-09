import { Component, OnInit, Input } from '@angular/core';
import {ITicketCount, TICKET_TYPES, TicketService} from '../services/ticket.service';

@Component({
  selector: 'app-ticket-display-panel',
  templateUrl: './ticket-display-panel.component.html',
  styleUrls: ['./ticket-display-panel.component.css']
})
export class TicketDisplayPanelComponent implements OnInit {
  @Input() restaurantId: number;
  ticketCount: ITicketCount[];

  constructor(private ticketService: TicketService) { }

  ngOnInit() {
    this.getTicketCount();
  }

  private ticketTypeSeatLabel(type: String): String {
    for (const typeEntry of TICKET_TYPES) {
      if (typeEntry.type === type) {
        return typeEntry.min_seat_no + '-' + typeEntry.max_seat_no;
      }
    }
  }

  private getTicketCount() {
    this.ticketService.getTicketCount(this.restaurantId).subscribe(
      (res: ITicketCount[]) => {
        this.ticketCount = res;
      }
    );
  }
}
